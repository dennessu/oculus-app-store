/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.core.service

import com.junbo.billing.clientproxy.IdentityFacade
import com.junbo.billing.clientproxy.PaymentFacade
import com.junbo.billing.core.publisher.AsyncChargePublisher
import com.junbo.billing.db.repository.BalanceRepository
import com.junbo.billing.spec.enums.BalanceStatus
import com.junbo.billing.spec.enums.BalanceType
import com.junbo.billing.spec.enums.EventActionType
import com.junbo.billing.spec.enums.TaxStatus
import com.junbo.billing.spec.error.AppErrors
import com.junbo.billing.spec.model.*
import com.junbo.common.id.BalanceId
import com.junbo.common.id.OrderId
import com.junbo.identity.spec.v1.model.User
import com.junbo.langur.core.promise.Promise
import com.junbo.payment.spec.enums.PIType
import com.junbo.payment.spec.model.PaymentInstrument
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.transaction.annotation.Transactional

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
    ShippingAddressService shippingAddressService

    @Autowired
    TransactionService transactionService

    @Autowired
    @Qualifier(value='billingIdentityFacade')
    IdentityFacade identityFacade

    @Autowired
    @Qualifier(value='billingPaymentFacade')
    PaymentFacade paymentFacade

    @Autowired
    TaxService taxService

    //@Autowired
    //AsyncChargePublisher asyncChargePublisher

    private static final Logger LOGGER = LoggerFactory.getLogger(BalanceServiceImpl)

    private static final Set<String> SUPPORT_ASYNC_CHARGE_PI_TYPE

    static {
        Set<String> supportAsyncChargePiType = [] as Set
        supportAsyncChargePiType << PIType.CREDITCARD.name()

        SUPPORT_ASYNC_CHARGE_PI_TYPE = Collections.unmodifiableSet(supportAsyncChargePiType)
    }

    @Override
    Promise<Balance> addBalance(Balance balance) {

        Balance tmpBalance = checkTrackingUUID(balance.trackingUuid)
        if (tmpBalance != null) {
            LOGGER.info('name=Add_Balance_Same_UUID. tracking uuid: {0}', balance.trackingUuid)
            return Promise.pure(tmpBalance)
        }

        return validateUser(balance).then {
            return validatePI(balance).then {
                validateBalanceType(balance)
                validateCurrency(balance)
                validateCountry(balance)
                validateBalance(balance, false)

                return taxService.calculateTax(balance).then { Balance taxedBalance ->
                    computeTotal(taxedBalance)
                    validateBalanceTotal(taxedBalance)

                    // set the balance status to INIT
                    taxedBalance.setStatus(BalanceStatus.INIT.name())
                    if (taxedBalance.isAsyncCharge == null) {
                        taxedBalance.isAsyncCharge = false
                    }

                    Balance savedBalance = balanceRepository.saveBalance(taxedBalance)

                    if (savedBalance.isAsyncCharge) {
                        LOGGER.info('name=Async_Charge_Balance. balance id: ' + savedBalance.balanceId.value)
                        //asyncChargePublisher.publish(savedBalance.balanceId.toString())
                        return Promise.pure(savedBalance)
                    }
                    return transactionService.processBalance(savedBalance).then { Balance returnedBalance ->
                        return Promise.pure(balanceRepository.updateBalance(returnedBalance, EventActionType.CHARGE))
                    }
                }
            }
        }
    }

    @Override
    Promise<Balance> quoteBalance(Balance balance) {

        return validateUser(balance).then {
            return validatePI(balance).then {
                validateBalanceType(balance)
                validateCurrency(balance)
                validateCountry(balance)
                validateBalance(balance, true)

                return taxService.calculateTax(balance).then { Balance taxedBalance ->
                    computeTotal(taxedBalance)
                    validateBalanceTotal(taxedBalance)

                    return Promise.pure(taxedBalance)
                }
            }
        }
    }

    @Override
    Promise<Balance> captureBalance(Balance balance) {

        Balance tmpBalance = checkTrackingUUID(balance.trackingUuid)
        if (tmpBalance != null) {
            LOGGER.info('name=Capture_Balance_Same_UUID. tracking uuid: {0}', balance.trackingUuid)
            return Promise.pure(tmpBalance)
        }

        if (balance.balanceId == null) {
            throw AppErrors.INSTANCE.fieldMissingValue('balanceId').exception()
        }
        Balance savedBalance = balanceRepository.getBalance(balance.balanceId.value)
        if (savedBalance == null) {
            throw AppErrors.INSTANCE.balanceNotFound(balance.balanceId.value.toString()).exception()
        }
        if (savedBalance.status != BalanceStatus.PENDING_CAPTURE.name()) {
            throw AppErrors.INSTANCE.invalidBalanceStatus(savedBalance.status).exception()
        }
        if (savedBalance.transactions.size() == 0) {
            throw AppErrors.INSTANCE.transactionNotFound(savedBalance.balanceId.value.toString()).exception()
        }
        if (balance.totalAmount != null && balance.totalAmount > savedBalance.totalAmount) {
            throw AppErrors.INSTANCE.invalidBalanceTotal(balance.totalAmount.toString()).exception()
        }

        return transactionService.captureBalance(savedBalance, balance.totalAmount).then {
            //persist the balance entity
            Balance resultBalance = balanceRepository.updateBalance(savedBalance, EventActionType.CAPTURE)
            return Promise.pure(resultBalance)
        }
    }

    @Override
    Promise<Balance> processAsyncBalance(Balance balance) {

        if (balance.balanceId == null) {
            throw AppErrors.INSTANCE.fieldMissingValue('balanceId').exception()
        }
        Balance savedBalance = balanceRepository.getBalance(balance.balanceId.value)
        if (savedBalance == null) {
            throw AppErrors.INSTANCE.balanceNotFound(balance.balanceId.value.toString()).exception()
        }
        if (savedBalance.status != BalanceStatus.INIT.name()) {
            throw AppErrors.INSTANCE.invalidBalanceStatus(savedBalance.status).exception()
        }
        if (savedBalance.isAsyncCharge != true) {
            throw AppErrors.INSTANCE.notAsyncChargeBalance(balance.balanceId.value.toString()).exception()
        }

        return transactionService.processBalance(savedBalance).then { Balance returnedBalance ->
            return Promise.pure(balanceRepository.updateBalance(returnedBalance, EventActionType.ASYNC_CHARGE))
        }
    }

    @Override
    Promise<Balance> getBalance(BalanceId balanceId) {
        if (balanceId == null) {
            throw AppErrors.INSTANCE.fieldMissingValue('balanceId').exception()
        }
        return Promise.pure(balanceRepository.getBalance(balanceId.value))
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

        if (balance.balanceId == null) {
            throw AppErrors.INSTANCE.fieldMissingValue('balanceId').exception()
        }
        if (balance.shippingAddressId == null) {
            throw AppErrors.INSTANCE.fieldMissingValue('shippingAddressId').exception()
        }
        Balance savedBalance = balanceRepository.getBalance(balance.balanceId.value)
        if (savedBalance == null) {
            throw AppErrors.INSTANCE.balanceNotFound(balance.balanceId.value.toString()).exception()
        }
        savedBalance.setShippingAddressId(balance.shippingAddressId)
        Balance resultBalance = balanceRepository.updateBalance(savedBalance, EventActionType.ADDRESS_CHANGE)
        return Promise.pure(resultBalance)
    }

    private Promise<Void> validateUser(Balance balance) {
        if (balance.userId == null) {
            throw AppErrors.INSTANCE.fieldMissingValue('userId').exception()
        }

        Long userId = balance.userId.value
        return identityFacade.getUser(userId).recover { Throwable throwable ->
            LOGGER.error('name=Error_Get_User. user id: ' + userId, throwable)
            throw AppErrors.INSTANCE.userNotFound(userId.toString()).exception()
        }.then { User user ->
            if (user == null) {
                LOGGER.error('name=Error_Get_User. Get null for the user id: {0}', userId)
                throw AppErrors.INSTANCE.userNotFound(userId.toString()).exception()
            }
            if (user.active == null || !user.active) {
                LOGGER.error('name=Error_Get_User. User not active with id: {0}', userId)
                throw AppErrors.INSTANCE.userStatusInvalid(userId.toString()).exception()
            }
            return Promise.pure(null)
        }
    }

    private Promise<Void> validatePI(Balance balance) {
        if (balance.piId == null) {
            throw AppErrors.INSTANCE.fieldMissingValue('piId').exception()
        }
        return paymentFacade.getPaymentInstrument(balance.piId.value)
                .recover { Throwable throwable ->
            LOGGER.error('name=Error_Get_PaymentInstrument. pi id: ' + balance.piId.value, throwable)
            throw AppErrors.INSTANCE.piNotFound(balance.piId.value.toString()).exception()
        }.then { PaymentInstrument pi ->
            if (!SUPPORT_ASYNC_CHARGE_PI_TYPE.contains(pi.type)) {
                LOGGER.info('name=Not_Support_Async_Charge. pi type: ' + pi.type)
                balance.isAsyncCharge = false
            }
            //todo: more validation for the PI
            return Promise.pure(null)
        }
    }

    private void validateBalanceType(Balance balance) {
        if (balance.type == null || balance.type.isEmpty()) {
            throw AppErrors.INSTANCE.fieldMissingValue('type').exception()
        }

        try {
            BalanceType.valueOf(balance.type)
        }
        catch (IllegalArgumentException ex) {
            throw AppErrors.INSTANCE.invalidBalanceType(balance.type).exception()
        }
    }

    private void validateCurrency(Balance balance) {
        if (balance.currency == null || balance.currency.isEmpty()) {
            throw AppErrors.INSTANCE.fieldMissingValue('currency').exception()
        }
        def currency = currencyService.getCurrencyByName(balance.currency)
        if (currency == null) {
            throw AppErrors.INSTANCE.currencyNotFound(balance.currency).exception()
        }
    }

    private void validateCountry(Balance balance) {
        if (balance.country == null || balance.country.isEmpty()) {
            throw AppErrors.INSTANCE.fieldMissingValue('country').exception()
        }
    }

    private void validateBalance(Balance balance, Boolean isQuote) {
        if (!isQuote && balance.orderId == null) {
            throw AppErrors.INSTANCE.fieldMissingValue('orderId').exception()
        }
        if (balance.balanceItems == null || balance.balanceItems.size() == 0) {
            throw AppErrors.INSTANCE.fieldMissingValue('balanceItems').exception()
        }
        balance.balanceItems.each { BalanceItem balanceItem ->
            if (!isQuote) {
                if (balanceItem.orderItemId == null) {
                    throw AppErrors.INSTANCE.fieldMissingValue('balanceItem.orderItemId').exception()
                }
                if (balanceItem.orderId == null) {
                    balanceItem.orderId = balance.orderId
                }
            }
            if (balanceItem.amount == null) {
                throw AppErrors.INSTANCE.fieldMissingValue('balanceItem.amount').exception()
            }
        }
    }

    private void validateBalanceTotal(Balance balance) {
        if (balance.totalAmount <= 0) {
            throw AppErrors.INSTANCE.invalidBalanceTotal(balance.totalAmount.toString()).exception()
        }
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
