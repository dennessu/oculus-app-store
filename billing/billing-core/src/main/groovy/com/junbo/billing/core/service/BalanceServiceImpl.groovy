/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.core.service

import com.junbo.billing.clientproxy.IdentityFacade
import com.junbo.billing.clientproxy.PaymentFacade
import com.junbo.billing.db.repository.BalanceRepository
import com.junbo.billing.spec.enums.BalanceStatus
import com.junbo.billing.spec.error.AppErrors
import com.junbo.billing.spec.model.Balance
import com.junbo.billing.spec.model.BalanceItem
import com.junbo.billing.spec.enums.BalanceType
import com.junbo.billing.spec.model.Currency
import com.junbo.billing.spec.model.DiscountItem
import com.junbo.billing.spec.model.TaxItem
import com.junbo.identity.spec.model.user.User
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
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
    IdentityFacade identityFacade

    @Autowired
    PaymentFacade paymentFacade

    @Autowired
    TaxService taxService

    @Override
    Promise<Balance> addBalance(Balance balance) {

        validateBalanceType(balance)
        validateUser(balance)
        validatePI(balance)
        validateCurrency(balance)
        validateCountry(balance)
        validateBalanceItem(balance)

        calculateTax(balance)
        computeTotal(balance)
        validateBalanceTotal(balance)

        // set the balance status to INIT
        balance.setStatus(BalanceStatus.INIT.name())

        transactionService.processBalance(balance)

        //persist the balance entity
        Balance resultBalance = balanceRepository.saveBalance(balance)

        return Promise.pure(resultBalance)
    }

    @Override
    Promise<Balance> quoteBalance(Balance balance) {

        validateBalanceItem(balance)

        calculateTax(balance)
        computeTotal(balance)

        return Promise.pure(balance)
    }

    @Override
    Promise<Balance> captureBalance(Balance balance) {

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

        transactionService.captureBalance(savedBalance, balance.totalAmount)

        savedBalance.setType(BalanceType.MANUAL_CAPTURE.name())
        Balance resultBalance = balanceRepository.updateBalance(savedBalance)
        return Promise.pure(resultBalance)
    }

    @Override
    Promise<Balance> getBalance(Long balanceId) {
        return Promise.pure(balanceRepository.getBalance(balanceId))
    }

    @Override
    Promise<List<Balance>> getBalances(Long orderId) {

        return Promise.pure(balanceRepository.getBalances(orderId))
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

    private void validateUser(Balance balance) {
        if (balance.userId == null) {
            throw AppErrors.INSTANCE.fieldMissingValue('userId').exception()
        }

        Long userId = balance.userId.value
        identityFacade.getUser(userId).then {
            User user = (User)it
            if (user == null) {
                throw AppErrors.INSTANCE.userNotFound(userId.toString()).exception()
            }
            if (user.status != 'ACTIVE') {
                throw AppErrors.INSTANCE.userStatusInvalid(userId.toString()).exception()
            }
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

    private void validatePI(Balance balance) {
        if (balance.piId == null) {
            throw AppErrors.INSTANCE.fieldMissingValue('piId').exception()
        }
        def promisePi = paymentFacade.getPaymentInstrument(balance.piId.value)
        if (promisePi == null) {
            throw AppErrors.INSTANCE.piNotFound(balance.piId.value.toString()).exception()
        }
    }

    private void validateBalanceItem(Balance balance) {
        if (balance.balanceItems == null || balance.balanceItems.size() == 0) {
            throw AppErrors.INSTANCE.fieldMissingValue('balanceItems').exception()
        }
        //todo: more validation for balance items
    }

    private void validateBalanceTotal(Balance balance) {
        if (balance.totalAmount <= 0) {
            throw AppErrors.INSTANCE.invalidBalanceTotal(balance.totalAmount.toString()).exception()
        }
    }

    private void calculateTax(Balance balance) {

        taxService.calculateTax(balance)
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

        if (!balance.taxIncluded) {
            amount = amount + taxTotal
        }
        amount = amount - discountTotal

        balance.setTaxAmount(taxTotal)
        balance.setDiscountAmount(discountTotal)

        amount = currency.getValueByBaseUnits(amount)
        balance.setTotalAmount(amount)
    }

}
