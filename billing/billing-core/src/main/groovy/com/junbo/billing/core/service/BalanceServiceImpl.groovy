/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.core.service

import com.junbo.authorization.AuthorizeContext
import com.junbo.authorization.AuthorizeService
import com.junbo.authorization.RightsScope
import com.junbo.billing.auth.BalanceAuthorizeCallbackFactory
import com.junbo.billing.core.publisher.AsyncChargePublisher
import com.junbo.billing.core.validator.BalanceValidator
import com.junbo.billing.db.repo.facade.BalanceRepositoryFacade
import com.junbo.billing.spec.enums.BalanceStatus
import com.junbo.billing.spec.enums.BalanceType
import com.junbo.billing.spec.enums.EventActionType
import com.junbo.billing.spec.enums.TaxStatus
import com.junbo.billing.spec.error.AppErrors
import com.junbo.billing.spec.model.Balance
import com.junbo.billing.spec.model.BalanceItem
import com.junbo.billing.spec.model.DiscountItem
import com.junbo.billing.spec.model.TaxItem
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.BalanceId
import com.junbo.common.id.OrderId
import com.junbo.common.id.PIType
import com.junbo.identity.spec.v1.model.Currency
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
class BalanceServiceImpl implements BalanceService {

    @Autowired
    BalanceRepositoryFacade balanceRepositoryFacade

    @Autowired
    AsyncChargePublisher asyncChargePublisher

    @Autowired
    PlatformTransactionManager transactionManager

    TransactionService transactionService

    @Autowired
    private BalanceAuthorizeCallbackFactory authorizeCallbackFactory

    @Autowired
    private AuthorizeService authorizeService

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
    @Transactional
    Promise<Balance> addBalance(Balance balance) {

        Balance tmpBalance = checkTrackingUUID(balance.trackingUuid)
        if (tmpBalance != null) {
            LOGGER.info('name=Add_Balance_Same_UUID. tracking uuid: {}', balance.trackingUuid)
            return Promise.pure(tmpBalance)
        }

        def callback = authorizeCallbackFactory.create(balance)
        return RightsScope.with(authorizeService.authorize(callback)) {
            if (!AuthorizeContext.hasRights('create')) {
                throw AppCommonErrors.INSTANCE.forbidden().exception()
            }

            Balance originalBalance = null
            if (balance.type == BalanceType.REFUND.name()) {
                if (!AuthorizeContext.hasRights('refund')) {
                    throw AppCommonErrors.INSTANCE.forbidden().exception()
                }

                balanceValidator.validateRefund(balance)

                originalBalance = balanceRepositoryFacade.getBalance(balance.originalBalanceId.value)
                if (originalBalance == null) {
                    throw AppErrors.INSTANCE.balanceNotFound("originalBalanceId", balance.originalBalanceId).exception()
                }
                balanceValidator.validateBalanceStatus(originalBalance.status,
                        [BalanceStatus.COMPLETED.name(), BalanceStatus.AWAITING_PAYMENT.name()])
                balanceValidator.validateTransactionNotEmpty(originalBalance.getId(), originalBalance.transactions)

                if (balance.balanceItems == null || balance.balanceItems.size() == 0) {
                    // if there is no balance items input, assume full refund
                    for (BalanceItem item : originalBalance.balanceItems) {
                        def refundItem = new BalanceItem()
                        refundItem.originalBalanceItemId = item.getId()
                        refundItem.amount = item.amount
                        refundItem.orderId = item.orderId
                        refundItem.orderItemId = item.orderItemId
                        balance.addBalanceItem(refundItem)
                    }
                }

            }

            return balanceValidator.validateUser(balance.userId).then {
                return balanceValidator.validateCountry(balance.country).then {
                    return balanceValidator.validateCurrency(balance.currency).then { Currency currency ->
                        return balanceValidator.validatePI(balance.piId).then { PaymentInstrument pi ->
                            balanceValidator.validateBalanceType(balance.type)
                            balanceValidator.validateBalance(balance, false)

                            if (balance.isAsyncCharge == true && !SUPPORT_ASYNC_CHARGE_PI_TYPE.contains(pi.type)) {
                                LOGGER.info('name=Not_Support_Async_Charge. pi type: ' + pi.type)
                                balance.isAsyncCharge = false
                            }

                            return taxService.calculateTax(balance).then { Balance taxedBalance ->
                                computeTotal(taxedBalance, currency.numberAfterDecimal)
                                balanceValidator.validateBalanceTotal(taxedBalance)

                                // set the balance status to INIT
                                taxedBalance.setStatus(BalanceStatus.INIT.name())
                                if (taxedBalance.isAsyncCharge == null) {
                                    taxedBalance.isAsyncCharge = false
                                }

                                Balance savedBalance = saveAndCommitBalance(taxedBalance)

                                if (savedBalance.isAsyncCharge) {
                                    LOGGER.info('name=Async_Charge_Balance. balance id: ' + savedBalance.getId().value)
                                    try {
                                        asyncChargePublisher.publish(savedBalance.id.toString())
                                    } catch (Exception ex) {
                                        LOGGER.error('name=Async_Charge_Balance_Queue_Error. ', ex)
                                        return Promise.pure(savedBalance)
                                    }
                                    savedBalance.setStatus(BalanceStatus.QUEUING.name())
                                    return Promise.pure(balanceRepositoryFacade.updateBalance(savedBalance, EventActionType.QUEUE))
                                }
                                return transactionService.processBalance(savedBalance, originalBalance).recover {
                                    Throwable throwable ->
                                        updateAndCommitBalance(savedBalance, EventActionType.CHARGE)
                                        throw throwable
                                }.then { Balance returnedBalance ->
                                    return Promise.pure(updateAndCommitBalance(returnedBalance, EventActionType.CHARGE))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    Promise<Balance> quoteBalance(Balance balance) {

        return balanceValidator.validateUser(balance.userId).then {
            def callback = authorizeCallbackFactory.create(balance)
            return RightsScope.with(authorizeService.authorize(callback)) {
                if (!AuthorizeContext.hasRights('quote')) {
                    throw AppCommonErrors.INSTANCE.forbidden().exception()
                }

                return balanceValidator.validatePI(balance.piId).then {
                    return balanceValidator.validateCountry(balance.country).then {
                        return balanceValidator.validateCurrency(balance.currency).then { Currency currency ->
                            balanceValidator.validateBalanceType(balance.type)
                            balanceValidator.validateBalance(balance, true)

                            return taxService.calculateTax(balance).then { Balance taxedBalance ->
                                computeTotal(taxedBalance, currency.numberAfterDecimal)
                                balanceValidator.validateBalanceTotal(taxedBalance)

                                return Promise.pure(taxedBalance)
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    @Transactional
    Promise<Balance> captureBalance(Balance balance) {

        Balance savedBalance = balanceValidator.validateBalanceId(balance.getId())
        def callback = authorizeCallbackFactory.create(balance)
        return RightsScope.with(authorizeService.authorize(callback)) {
            if (!AuthorizeContext.hasRights('capture')) {
                throw AppCommonErrors.INSTANCE.forbidden().exception()
            }

            balanceValidator.validateBalanceStatus(savedBalance.status, BalanceStatus.PENDING_CAPTURE.name())
            balanceValidator.validateTransactionNotEmpty(savedBalance.getId(), savedBalance.transactions)

            if (balance.totalAmount != null && balance.totalAmount > savedBalance.totalAmount) {
                throw AppErrors.INSTANCE.invalidBalanceTotal(balance.totalAmount.toString()).exception()
            }

            return transactionService.captureBalance(savedBalance, balance.totalAmount).recover { Throwable throwable ->
                updateAndCommitBalance(savedBalance, EventActionType.CAPTURE)
                throw throwable
            }.then { Balance capturedBalance ->
                //persist the balance entity
                return Promise.pure(updateAndCommitBalance(capturedBalance, EventActionType.CAPTURE))
            }
        }
    }

    @Override
    @Transactional
    Promise<Balance> confirmBalance(Balance balance) {

        Balance savedBalance = balanceValidator.validateBalanceId(balance.getId())
        def callback = authorizeCallbackFactory.create(balance)
        return RightsScope.with(authorizeService.authorize(callback)) {
            if (!AuthorizeContext.hasRights('confirm')) {
                throw AppCommonErrors.INSTANCE.forbidden().exception()
            }

            balanceValidator.validateBalanceStatus(savedBalance.status, BalanceStatus.UNCONFIRMED.name())
            balanceValidator.validateTransactionNotEmpty(savedBalance.getId(), savedBalance.transactions)

            return transactionService.confirmBalance(savedBalance).recover { Throwable throwable ->
                updateAndCommitBalance(savedBalance, EventActionType.CONFIRM)
                throw throwable
            }.then { Balance confirmedBalance ->
                //persist the balance entity
                return Promise.pure(updateAndCommitBalance(confirmedBalance, EventActionType.CONFIRM))
            }
        }
    }

    @Override
    @Transactional
    Promise<Balance> checkBalance(Balance balance) {

        Balance savedBalance = balanceValidator.validateBalanceId(balance.getId())
        def callback = authorizeCallbackFactory.create(balance)
        return RightsScope.with(authorizeService.authorize(callback)) {
            if (!AuthorizeContext.hasRights('check')) {
                throw AppCommonErrors.INSTANCE.forbidden().exception()
            }

            balanceValidator.validateBalanceStatus(savedBalance.status,
                    [BalanceStatus.UNCONFIRMED.name(), BalanceStatus.AWAITING_PAYMENT.name(), BalanceStatus.INIT.name()])

            if (savedBalance.status == BalanceStatus.INIT.name()) {
                return processAsyncBalance(balance)
            } else {
                balanceValidator.validateTransactionNotEmpty(savedBalance.getId(), savedBalance.transactions)

                return transactionService.checkBalance(savedBalance).recover { Throwable throwable ->
                    updateAndCommitBalance(savedBalance, EventActionType.CHECK)
                    throw throwable
                }.then { Balance checkedBalance ->
                    //persist the balance entity
                    return Promise.pure(updateAndCommitBalance(checkedBalance, EventActionType.CHECK))
                }
            }
        }
    }

    @Override
    @Transactional
    Promise<Balance> processAsyncBalance(Balance balance) {

        Balance savedBalance = balanceValidator.validateBalanceId(balance.getId())
        def callback = authorizeCallbackFactory.create(savedBalance)
        return RightsScope.with(authorizeService.authorize(callback)) {
            if (!AuthorizeContext.hasRights('process-async')) {
                throw AppCommonErrors.INSTANCE.forbidden().exception()
            }

            balanceValidator.validateBalanceStatus(savedBalance.status,
                    [BalanceStatus.INIT.name(), BalanceStatus.QUEUING.name()])
            if (savedBalance.isAsyncCharge != true) {
                throw AppErrors.INSTANCE.notAsyncChargeBalance(savedBalance.getId()).exception()
            }

            Balance originalBalance = null
            if (savedBalance.type == BalanceType.REFUND.name()) {
                originalBalance = balanceRepositoryFacade.getBalance(savedBalance.originalBalanceId.value)
            }
            return transactionService.processBalance(savedBalance, originalBalance).recover { Throwable throwable ->
                updateAndCommitBalance(savedBalance, EventActionType.ASYNC_CHARGE)
                throw throwable
            }.then { Balance returnedBalance ->
                return Promise.pure(updateAndCommitBalance(returnedBalance, EventActionType.ASYNC_CHARGE))
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    Promise<Balance> getBalance(BalanceId balanceId) {
        Balance savedBalance = balanceValidator.validateBalanceId(balanceId)
        def callback = authorizeCallbackFactory.create(savedBalance)
        return RightsScope.with(authorizeService.authorize(callback)) {
            if (!AuthorizeContext.hasRights('read')) {
                throw AppCommonErrors.INSTANCE.forbidden().exception()
            }

            return Promise.pure(savedBalance)
        }
    }

    @Override
    @Transactional(readOnly = true)
    Promise<List<Balance>> getBalances(OrderId orderId) {
        if (orderId == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('orderId').exception()
        }

        List<Balance> results = []
        List<Balance> balances = balanceRepositoryFacade.getBalances(orderId.value)
        balances.each { Balance balance ->
            def callback = authorizeCallbackFactory.create(balance)
            RightsScope.with(authorizeService.authorize(callback)) {
                if (AuthorizeContext.hasRights('read')) {
                    results.add(balance)
                }
                return Promise.pure(null)
            }
        }
        return Promise.pure(results)
    }

    private Balance checkTrackingUUID(UUID uuid) {
        if (uuid == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('trackingUuid').exception()
        }
        return balanceRepositoryFacade.getBalanceByUuid(uuid)
    }

    @Override
    @Transactional
    Promise<Balance> putBalance(Balance balance) {

        if (balance.shippingAddressId == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('shippingAddressId').exception()
        }
        Balance savedBalance = balanceValidator.validateBalanceId(balance.getId())
        def callback = authorizeCallbackFactory.create(savedBalance)
        return RightsScope.with(authorizeService.authorize(callback)) {
            if (!AuthorizeContext.hasRights('put')) {
                throw AppCommonErrors.INSTANCE.forbidden().exception()
            }

            savedBalance.setShippingAddressId(balance.shippingAddressId)
            Balance resultBalance = balanceRepositoryFacade.updateBalance(savedBalance, EventActionType.ADDRESS_CHANGE)
            return Promise.pure(resultBalance)
        }
    }

    protected Balance saveAndCommitBalance(final Balance balance) {
        AsyncTransactionTemplate template = new AsyncTransactionTemplate(transactionManager)
        template.setPropagationBehavior(3)
        return template.execute(new TransactionCallback<Balance>() {
            public Balance doInTransaction(TransactionStatus txnStatus) {
                return balanceRepositoryFacade.saveBalance(balance)
            }
        } )
    }

    protected Balance updateAndCommitBalance(final Balance balance, final EventActionType eventActionType) {
        AsyncTransactionTemplate template = new AsyncTransactionTemplate(transactionManager)
        template.setPropagationBehavior(3)
        return template.execute(new TransactionCallback<Balance>() {
            public Balance doInTransaction(TransactionStatus txnStatus) {
                return balanceRepositoryFacade.updateBalance(balance, eventActionType)
            }
        } )
    }

    private void computeTotal(Balance balance, Integer numberAfterDecimal) {

        if (numberAfterDecimal == null) {
            numberAfterDecimal = 2
        }

        BigDecimal amount = BigDecimal.ZERO
        BigDecimal discountTotal = BigDecimal.ZERO
        BigDecimal taxTotal = BigDecimal.ZERO

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

            tax = tax.setScale(numberAfterDecimal, BigDecimal.ROUND_HALF_EVEN)
            discount = discount.setScale(numberAfterDecimal, BigDecimal.ROUND_HALF_EVEN)
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

        amount = amount.setScale(numberAfterDecimal, BigDecimal.ROUND_HALF_EVEN)
        balance.setTotalAmount(amount)
    }
}
