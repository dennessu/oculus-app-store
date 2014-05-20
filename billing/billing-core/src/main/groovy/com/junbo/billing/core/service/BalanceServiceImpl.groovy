/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.core.service

import com.junbo.billing.core.publisher.AsyncChargePublisher
import com.junbo.billing.core.validator.BalanceValidator
import com.junbo.billing.db.repository.BalanceRepository
import com.junbo.billing.spec.enums.BalanceStatus
import com.junbo.billing.spec.enums.BalanceType
import com.junbo.billing.spec.enums.EventActionType
import com.junbo.billing.spec.enums.TaxStatus
import com.junbo.billing.spec.error.AppErrors
import com.junbo.billing.spec.model.*
import com.junbo.common.id.BalanceId
import com.junbo.common.id.OrderId
import com.junbo.common.id.PIType
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.transaction.AsyncTransactionTemplate
import com.junbo.payment.spec.model.PaymentInstrument
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionStatus
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionCallback

/**
 * Created by xmchen on 14-1-26.
 */
@CompileStatic
@Transactional
class BalanceServiceImpl implements BalanceService {

    @Autowired
    BalanceRepository balanceRepository


    @Autowired
    CurrencyService currencyService

    @Autowired
    AsyncChargePublisher asyncChargePublisher

    @Autowired
    PlatformTransactionManager transactionManager

    TransactionService transactionService

    @Autowired
    void setTransactionService(TransactionService transactionService) {
        this.transactionService = transactionService
    }

    BalanceValidator balanceValidator

    @Autowired
    void setBalanceValidator(BalanceValidator balanceValidator) {
        this.balanceValidator = balanceValidator
    }

    TaxService taxService

    @Autowired
    void setTaxService(TaxService taxService) {
        this.taxService = taxService
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(BalanceServiceImpl)

    private static final Set<Long> SUPPORT_ASYNC_CHARGE_PI_TYPE

    static {
        Set<Long> supportAsyncChargePiType = [] as Set
        supportAsyncChargePiType << PIType.CREDITCARD.id

        SUPPORT_ASYNC_CHARGE_PI_TYPE = Collections.unmodifiableSet(supportAsyncChargePiType)
    }

    @Override
    Promise<Balance> addBalance(Balance balance) {

        Balance tmpBalance = checkTrackingUUID(balance.trackingUuid)
        if (tmpBalance != null) {
            LOGGER.info('name=Add_Balance_Same_UUID. tracking uuid: {}', balance.trackingUuid)
            return Promise.pure(tmpBalance)
        }

        if (balance.type == BalanceType.REFUND.name()) {
            balanceValidator.validateRefund(balance)
        }

        return balanceValidator.validateUser(balance.userId).then {
            return balanceValidator.validatePI(balance.piId).then { PaymentInstrument pi ->
                balanceValidator.validateBalanceType(balance.type)
                balanceValidator.validateCurrency(balance.currency)
                balanceValidator.validateCountry(balance.country)
                balanceValidator.validateBalance(balance, false)

                if (balance.isAsyncCharge == true && !SUPPORT_ASYNC_CHARGE_PI_TYPE.contains(pi.type)) {
                    LOGGER.info('name=Not_Support_Async_Charge. pi type: ' + pi.type)
                    balance.isAsyncCharge = false
                }

                return taxService.calculateTax(balance).then { Balance taxedBalance ->
                    computeTotal(taxedBalance)
                    balanceValidator.validateBalanceTotal(taxedBalance)

                    // set the balance status to INIT
                    taxedBalance.setStatus(BalanceStatus.INIT.name())
                    if (taxedBalance.isAsyncCharge == null) {
                        taxedBalance.isAsyncCharge = false
                    }

                    Balance savedBalance = saveAndCommitBalance(taxedBalance)

                    if (savedBalance.isAsyncCharge) {
                        LOGGER.info('name=Async_Charge_Balance. balance id: ' + savedBalance.balanceId.value)
                        try {
                            asyncChargePublisher.publish(savedBalance.balanceId.toString())
                        } catch (Exception ex) {
                            LOGGER.error('name=Async_Charge_Balance_Queue_Error. ', ex)
                            return Promise.pure(savedBalance)
                        }
                        savedBalance.setStatus(BalanceStatus.QUEUING.name())
                        return Promise.pure(balanceRepository.updateBalance(savedBalance, EventActionType.QUEUE))
                    }
                    return transactionService.processBalance(savedBalance).recover { Throwable throwable ->
                        updateAndCommitBalance(savedBalance, EventActionType.CHARGE)
                        throw throwable
                    }.then { Balance returnedBalance ->
                        return Promise.pure(balanceRepository.updateBalance(returnedBalance, EventActionType.CHARGE))
                    }
                }
            }
        }
    }

    @Override
    Promise<Balance> quoteBalance(Balance balance) {

        return balanceValidator.validateUser(balance.userId).then {
            return balanceValidator.validatePI(balance.piId).then {
                balanceValidator.validateBalanceType(balance.type)
                balanceValidator.validateCurrency(balance.currency)
                balanceValidator.validateCountry(balance.country)
                balanceValidator.validateBalance(balance, true)

                return taxService.calculateTax(balance).then { Balance taxedBalance ->
                    computeTotal(taxedBalance)
                    balanceValidator.validateBalanceTotal(taxedBalance)

                    return Promise.pure(taxedBalance)
                }
            }
        }
    }

    @Override
    Promise<Balance> captureBalance(Balance balance) {

        Balance savedBalance = balanceValidator.validateBalanceId(balance.balanceId)
        balanceValidator.validateBalanceStatus(savedBalance.status, BalanceStatus.PENDING_CAPTURE.name())
        balanceValidator.validateTransactionNotEmpty(savedBalance.balanceId, savedBalance.transactions)

        if (balance.totalAmount != null && balance.totalAmount > savedBalance.totalAmount) {
            throw AppErrors.INSTANCE.invalidBalanceTotal(balance.totalAmount.toString()).exception()
        }

        return transactionService.captureBalance(savedBalance, balance.totalAmount).recover { Throwable throwable ->
            updateAndCommitBalance(savedBalance, EventActionType.CAPTURE)
            throw throwable
        }.then {
            //persist the balance entity
            Balance resultBalance = balanceRepository.updateBalance(savedBalance, EventActionType.CAPTURE)
            return Promise.pure(resultBalance)
        }
    }

    @Override
    Promise<Balance> confirmBalance(Balance balance) {

        Balance savedBalance = balanceValidator.validateBalanceId(balance.balanceId)
        balanceValidator.validateBalanceStatus(savedBalance.status, BalanceStatus.UNCONFIRMED.name())
        balanceValidator.validateTransactionNotEmpty(savedBalance.balanceId, savedBalance.transactions)

        return transactionService.confirmBalance(savedBalance).recover { Throwable throwable ->
            updateAndCommitBalance(savedBalance, EventActionType.CONFIRM)
            throw throwable
        }.then {
            //persist the balance entity
            Balance resultBalance = balanceRepository.updateBalance(savedBalance, EventActionType.CONFIRM)
            return Promise.pure(resultBalance)
        }
    }

    @Override
    Promise<Balance> checkBalance(Balance balance) {

        Balance savedBalance = balanceValidator.validateBalanceId(balance.balanceId)
        balanceValidator.validateBalanceStatus(savedBalance.status,
                [BalanceStatus.UNCONFIRMED.name(), BalanceStatus.AWAITING_PAYMENT.name(), BalanceStatus.INIT.name()])

        if (savedBalance.status == BalanceStatus.INIT.name()) {
            return processAsyncBalance(balance)
        } else {
            balanceValidator.validateTransactionNotEmpty(savedBalance.balanceId, savedBalance.transactions)

            return transactionService.checkBalance(savedBalance).recover { Throwable throwable ->
                updateAndCommitBalance(savedBalance, EventActionType.CONFIRM)
                throw throwable
            }.then {
                //persist the balance entity
                Balance resultBalance = balanceRepository.updateBalance(savedBalance, EventActionType.CHECK)
                return Promise.pure(resultBalance)
            }
        }
    }

    @Override
    Promise<Balance> processAsyncBalance(Balance balance) {

        Balance savedBalance = balanceValidator.validateBalanceId(balance.balanceId)
        balanceValidator.validateBalanceStatus(savedBalance.status,
                [BalanceStatus.INIT.name(), BalanceStatus.QUEUING.name()])
        if (savedBalance.isAsyncCharge != true) {
            throw AppErrors.INSTANCE.notAsyncChargeBalance(balance.balanceId.value.toString()).exception()
        }

        return transactionService.processBalance(savedBalance).recover { Throwable throwable ->
            updateAndCommitBalance(savedBalance, EventActionType.ASYNC_CHARGE)
            throw throwable
        }.then { Balance returnedBalance ->
            return Promise.pure(balanceRepository.updateBalance(returnedBalance, EventActionType.ASYNC_CHARGE))
        }
    }

    @Override
    Promise<Balance> getBalance(BalanceId balanceId) {
        Balance savedBalance = balanceValidator.validateBalanceId(balanceId)
        return Promise.pure(savedBalance)
    }

    @Override
    Promise<List<Balance>> getBalances(OrderId orderId) {
        if (orderId == null) {
            throw AppErrors.INSTANCE.fieldMissingValue('orderId').exception()
        }
        return Promise.pure(balanceRepository.getBalances(orderId.value))
    }

    private Balance checkTrackingUUID(UUID uuid) {
        if (uuid == null) {
            throw AppErrors.INSTANCE.fieldMissingValue('trackingUuid').exception()
        }
        return balanceRepository.getBalanceByUuid(uuid)
    }

    @Override
    Promise<Balance> putBalance(Balance balance) {

        if (balance.shippingAddressId == null) {
            throw AppErrors.INSTANCE.fieldMissingValue('shippingAddressId').exception()
        }
        Balance savedBalance = balanceValidator.validateBalanceId(balance.balanceId)
        savedBalance.setShippingAddressId(balance.shippingAddressId)
        Balance resultBalance = balanceRepository.updateBalance(savedBalance, EventActionType.ADDRESS_CHANGE)
        return Promise.pure(resultBalance)
    }

    protected Balance saveAndCommitBalance(final Balance balance) {
        AsyncTransactionTemplate template = new AsyncTransactionTemplate(transactionManager)
        template.setPropagationBehavior(3)
        return template.execute(new TransactionCallback<Balance>() {
            public Balance doInTransaction(TransactionStatus txnStatus) {
                return balanceRepository.saveBalance(balance)
            }
        } )
    }

    protected Balance updateAndCommitBalance(final Balance balance, final EventActionType eventActionType) {
        AsyncTransactionTemplate template = new AsyncTransactionTemplate(transactionManager)
        template.setPropagationBehavior(3)
        return template.execute(new TransactionCallback<Balance>() {
            public Balance doInTransaction(TransactionStatus txnStatus) {
                return balanceRepository.updateBalance(balance, eventActionType)
            }
        } )
    }

    private void computeTotal(Balance balance) {

        BigDecimal amount = BigDecimal.ZERO
        BigDecimal discountTotal = BigDecimal.ZERO
        BigDecimal taxTotal = BigDecimal.ZERO

        Currency currency = currencyService.getCurrencyByName(balance.currency)

        balance.balanceItems.each { BalanceItem item ->
            BigDecimal discount = BigDecimal.ZERO
            BigDecimal tax = BigDecimal.ZERO

            item.taxItems.each { TaxItem taxItem ->
                if (taxItem.taxAmount != null) {
                    tax = tax + taxItem.taxAmount
                }
            }
            item.discountItems.each { DiscountItem discountItem ->
                if (discountItem.discountAmount != null) {
                    discount = discount + discountItem.discountAmount
                }
            }

            tax = currency.getValueByBaseUnits(tax)
            discount = currency.getValueByBaseUnits(discount)
            item.setTaxAmount(tax)
            item.setDiscountAmount(discount)

            taxTotal = taxTotal + tax
            discountTotal = discountTotal + discount

            amount = amount + item.amount
        }

        if (balance.taxStatus == TaxStatus.TAXED.name() && !balance.taxIncluded) {
            amount = amount + taxTotal
        }
        //does not subtract the discount from amount, because the item amount has been discounted
        //amount = amount - discountTotal

        balance.setTaxAmount(taxTotal)
        balance.setDiscountAmount(discountTotal)

        amount = currency.getValueByBaseUnits(amount)
        balance.setTotalAmount(amount)
    }
}
