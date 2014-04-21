/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.core.service

import com.junbo.billing.clientproxy.PaymentFacade
import com.junbo.billing.db.repository.TransactionRepository
import com.junbo.billing.spec.enums.BalanceStatus
import com.junbo.billing.spec.enums.BalanceType
import com.junbo.billing.spec.enums.TransactionStatus
import com.junbo.billing.spec.enums.TransactionType
import com.junbo.billing.spec.error.AppErrors
import com.junbo.billing.spec.model.Balance
import com.junbo.billing.spec.model.Transaction
import com.junbo.langur.core.promise.Promise
import com.junbo.payment.spec.enums.PaymentStatus
import com.junbo.payment.spec.model.ChargeInfo
import com.junbo.payment.spec.model.PaymentTransaction
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Created by xmchen on 14-2-19.
 */
@CompileStatic
@Transactional
class TransactionServiceImpl implements TransactionService {
    @Autowired
    TransactionRepository transactionRepository

    @Autowired
    PaymentFacade paymentFacade

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionServiceImpl)

    @Override
    Promise<Balance> processBalance(Balance balance) {

        BalanceType balanceType = BalanceType.valueOf(balance.type)
        switch (balanceType) {
            case BalanceType.DEBIT:
                return charge(balance)
            case BalanceType.MANUAL_CAPTURE:
                return authorize(balance)
            default:
                throw AppErrors.INSTANCE.invalidBalanceType(balance.type).exception()
        }
    }

    @Override
    Promise<Balance> captureBalance(Balance balance, BigDecimal amount) {

        def paymentTransaction = generatePaymentTransaction(balance)
        if (amount != null) {
            paymentTransaction.chargeInfo.setAmount(amount)
        }
        String paymentRefId = balance.transactions[0].paymentRefId
        Long paymentId
        try {
            paymentId = Long.parseLong(paymentRefId)
        } catch (NumberFormatException ex) {
            throw AppErrors.INSTANCE.invalidPaymentId(paymentRefId).exception()
        }

        def newTransaction = new Transaction()
        newTransaction.setAmount(amount)
        newTransaction.setCurrency(balance.currency)
        newTransaction.setType(TransactionType.CAPTURE.name())
        newTransaction.setPiId(balance.piId)

        LOGGER.info('name=Capture_Balance. balance currency: {0}, authed amount: {1}, settled amount: {1}, pi id: {3}',
                balance.currency, balance.totalAmount, amount, balance.piId)
        return paymentFacade.postPaymentCapture(paymentId, paymentTransaction).recover { Throwable throwable ->
            LOGGER.error('name=Capture_Balance_Error. error in post payment capture', throwable)
            newTransaction.setStatus(TransactionStatus.ERROR.name())
            balance.addTransaction(newTransaction)
            balance.setStatus(BalanceStatus.ERROR.name())
            return Promise.pure(null)
        }.then { PaymentTransaction pt ->
            if (pt == null) {
                return Promise.pure(balance)
            }

            LOGGER.info('name=Capture_Balance. payment id: {0}, amount: {1}, status: {2}',
                    pt.id, pt.chargeInfo.amount, pt.status)
            newTransaction.setPaymentRefId(pt.id.toString())
            newTransaction.setAmount(pt.chargeInfo.amount)
            if (pt.status == PaymentStatus.SETTLEMENT_SUBMITTED.name()) {
                newTransaction.setStatus(TransactionStatus.SUCCESS.name())
                balance.setStatus(BalanceStatus.AWAITING_PAYMENT.name())
            } else if (pt.status == PaymentStatus.SETTLEMENT_DECLINED) {
                newTransaction.setStatus(TransactionStatus.DECLINE.name())
                balance.setStatus(BalanceStatus.FAILED.name())
            } else {
                newTransaction.setStatus(TransactionStatus.ERROR.name())
                balance.setStatus(BalanceStatus.ERROR.name())
            }
            balance.addTransaction(newTransaction)
            return Promise.pure(balance)
        }
    }

    private Promise<Balance> charge(Balance balance) {

        def transaction = new Transaction()
        transaction.setAmount(balance.totalAmount)
        transaction.setCurrency(balance.currency)
        transaction.setType(TransactionType.CHARGE.name())
        transaction.setPiId(balance.piId)

        def paymentTransaction = generatePaymentTransaction(balance)
        LOGGER.info('name=Charge_Balance. balance currency: {0}, amount: {1}, pi id: {2}',
                balance.currency, balance.totalAmount, balance.piId)
        return paymentFacade.postPaymentCharge(paymentTransaction).recover { Throwable throwable ->
            LOGGER.error('name=Charge_Balance_Error. error in post payment charge', throwable)
            transaction.setStatus(TransactionStatus.ERROR.name())
            balance.addTransaction(transaction)
            balance.setStatus(BalanceStatus.ERROR.name())
            return Promise.pure(null)
        }.then { PaymentTransaction pt ->
            if (pt == null) {
                return Promise.pure(balance)
            }

            LOGGER.info('name=Charge_Balance. payment id: {0}, status: {1}', pt.id, pt.status)
            transaction.setPaymentRefId(pt.id.toString())
            PaymentStatus paymentStatus = PaymentStatus.valueOf(pt.status)
            switch (paymentStatus) {
                case PaymentStatus.SETTLEMENT_SUBMITTED:
                    transaction.setStatus(TransactionStatus.SUCCESS.name())
                    balance.setStatus(BalanceStatus.AWAITING_PAYMENT.name())
                    break
                case PaymentStatus.SETTLEMENT_DECLINED:
                    transaction.setStatus(TransactionStatus.DECLINE.name())
                    balance.setStatus(BalanceStatus.FAILED.name())
                    break
                default:
                    transaction.setStatus(TransactionStatus.ERROR.name())
                    balance.setStatus(BalanceStatus.ERROR.name())
                    break
            }
            balance.addTransaction(transaction)
            return Promise.pure(balance)
        }
    }

    private Promise<Balance> authorize(Balance balance) {

        def transaction = new Transaction()
        transaction.setAmount(balance.totalAmount)
        transaction.setCurrency(balance.currency)
        transaction.setType(TransactionType.AUTHORIZE.name())
        transaction.setPiId(balance.piId)

        def paymentTransaction = generatePaymentTransaction(balance)
        LOGGER.info('name=Authorize_Balance. balance currency: {0}, amount: {1}, pi id: {2}',
                balance.currency, balance.totalAmount, balance.piId)
        return paymentFacade.postPaymentAuthorization(paymentTransaction).recover { Throwable throwable ->
            LOGGER.error('name=Authorize_Balance_Error. error in post payment authorization', throwable)
            transaction.setStatus(TransactionStatus.ERROR.name())
            balance.addTransaction(transaction)
            balance.setStatus(BalanceStatus.ERROR.name())
            return Promise.pure(null)
        }.then { PaymentTransaction pt ->
            if (pt == null) {
                return Promise.pure(balance)
            }

            LOGGER.info('name=Authorize_Balance. payment id: {0}, status: {1}', pt.id, pt.status)
            transaction.setPaymentRefId(pt.id.toString())
            PaymentStatus paymentStatus = PaymentStatus.valueOf(pt.status)
            switch (paymentStatus) {
                case PaymentStatus.AUTHORIZED:
                    transaction.setStatus(TransactionStatus.SUCCESS.name())
                    balance.setStatus(BalanceStatus.PENDING_CAPTURE.name())
                    break
                case PaymentStatus.AUTH_DECLINED:
                    transaction.setStatus(TransactionStatus.DECLINE.name())
                    balance.setStatus(BalanceStatus.FAILED.name())
                    break
                default:
                    transaction.setStatus(TransactionStatus.ERROR.name())
                    balance.setStatus(BalanceStatus.ERROR.name())
                    break
            }
            balance.addTransaction(transaction)
            return Promise.pure(balance)
        }
    }

    private PaymentTransaction generatePaymentTransaction(Balance balance) {
        def paymentTransaction = new PaymentTransaction()

        paymentTransaction.setTrackingUuid(UUID.randomUUID())
        paymentTransaction.setUserId(balance.userId.value)
        paymentTransaction.setPaymentInstrumentId(balance.piId.value)
        paymentTransaction.setBillingRefId(balance.balanceId.toString())

        def chargeInfo = new ChargeInfo()
        chargeInfo.setCurrency(balance.currency)
        chargeInfo.setAmount(balance.totalAmount)
        chargeInfo.setCountry(balance.country)
        paymentTransaction.setChargeInfo(chargeInfo)

        return paymentTransaction
    }
}
