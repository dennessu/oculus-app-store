/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.core.service

import com.junbo.billing.clientproxy.PaymentFacade
import com.junbo.billing.db.repository.TransactionRepository
import com.junbo.billing.spec.enums.BalanceStatus
import com.junbo.billing.spec.enums.TransactionStatus
import com.junbo.billing.spec.error.AppErrors
import com.junbo.billing.spec.model.Balance
import com.junbo.billing.spec.enums.BalanceType
import com.junbo.billing.spec.model.Transaction
import com.junbo.billing.spec.enums.TransactionType
import com.junbo.common.id.PaymentInstrumentId
import com.junbo.langur.core.promise.Promise
import com.junbo.payment.spec.enums.PaymentStatus
import com.junbo.payment.spec.model.ChargeInfo
import com.junbo.payment.spec.model.PaymentTransaction
import groovy.transform.CompileStatic
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

    @Override
    Promise<Balance> processBalance(Balance balance) {

        BalanceType balanceType = BalanceType.valueOf(balance.type)
        switch (balanceType) {
            case BalanceType.DEBIT:
                return charge(balance)
            case BalanceType.DELAY_DEBIT:
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
        def transaction = balance.transactions[0]
        String paymentRefId = transaction.paymentRefId
        Long paymentId
        try {
            paymentId = Long.parseLong(paymentRefId)
        } catch (NumberFormatException ex) {
            throw AppErrors.INSTANCE.invalidPaymentId(paymentRefId).exception()
        }

        return paymentFacade.postPaymentCapture(paymentId, paymentTransaction).recover { Throwable throwable ->
            transaction.setStatus(TransactionStatus.ERROR.name())
            balance.setStatus(BalanceStatus.ERROR.name())
        }.then { PaymentTransaction pt ->
            transaction.setType(TransactionType.CAPTURE.name())
            transaction.setAmount(pt.chargeInfo.amount)
            if (pt.status == PaymentStatus.SETTLEMENT_SUBMITTED.name()) {
                transaction.setStatus(TransactionStatus.SUCCESS.name())
                balance.setStatus(BalanceStatus.AWAITING_PAYMENT.name())
            } else if (pt.status == PaymentStatus.SETTLEMENT_DECLINED) {
                transaction.setStatus(TransactionStatus.DECLINE.name())
                balance.setStatus(BalanceStatus.FAILED.name())
            } else {
                transaction.setStatus(TransactionStatus.ERROR.name())
                balance.setStatus(BalanceStatus.ERROR.name())
            }
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
        return paymentFacade.postPaymentCharge(paymentTransaction).recover { Throwable throwable ->
            transaction.setStatus(TransactionStatus.ERROR.name())
            balance.addTransaction(transaction)
            balance.setStatus(BalanceStatus.ERROR.name())
            return Promise.pure(balance)
        }
        .then { PaymentTransaction pt ->
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
        return paymentFacade.postPaymentAuthorization(paymentTransaction).recover { Throwable throwable ->
            transaction.setStatus(TransactionStatus.ERROR.name())
            balance.addTransaction(transaction)
            balance.setStatus(BalanceStatus.ERROR.name())
            return Promise.pure(balance)
        }
        .then { PaymentTransaction pt ->
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

        def chargeInfo = new ChargeInfo()
        chargeInfo.setCurrency(balance.currency)
        chargeInfo.setAmount(balance.totalAmount)
        chargeInfo.setCountry(balance.country)
        paymentTransaction.setChargeInfo(chargeInfo)

        return paymentTransaction
    }
}
