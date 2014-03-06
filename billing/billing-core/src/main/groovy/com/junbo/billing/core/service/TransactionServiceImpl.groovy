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
    Balance processBalance(Balance balance) {

        def paymentTransaction = generatePaymentTransaction(balance)
        TransactionType transactionType
        Promise<PaymentTransaction> promiseResponse
        BalanceType balanceType = BalanceType.valueOf(balance.type)
        switch (balanceType) {
            case BalanceType.DEBIT:
                promiseResponse = paymentFacade.postPaymentCharge(paymentTransaction)
                transactionType = TransactionType.CHARGE
                break
            case BalanceType.DELAY_DEBIT:
                promiseResponse = paymentFacade.postPaymentAuthorization(paymentTransaction)
                transactionType = TransactionType.AUTHORIZE
                break
            default:
                throw AppErrors.INSTANCE.invalidBalanceType(balance.type).exception()
        }

        def transaction = new Transaction()
        transaction.setAmount(balance.totalAmount)
        transaction.setCurrency(balance.currency)
        transaction.setType(transactionType.name())
        transaction.setPiId(balance.piId)

        if (promiseResponse != null) {
            promiseResponse.then { PaymentTransaction pt ->
                TransactionStatus transactionStatus

                transaction.setPaymentRefId(pt.paymentId.toString())
                PaymentStatus paymentStatus = PaymentStatus.valueOf(pt.status)
                switch (paymentStatus) {
                    case PaymentStatus.AUTHORIZED:
                    case PaymentStatus.SETTLEMENT_SUBMITTED:
                    case PaymentStatus.REVERSED:
                        transactionStatus = TransactionStatus.SUCCESS
                        break
                    case PaymentStatus.AUTH_DECLINED:
                    case PaymentStatus.REVERSE_DECLINED:
                    case PaymentStatus.SETTLEMENT_DECLINED:
                        transactionStatus = TransactionStatus.DECLINE
                        break
                    default:
                        transactionStatus = TransactionStatus.ERROR
                        break
                }
                transaction.setStatus(transactionStatus.name())

                BalanceStatus balanceStatus
                switch (transactionStatus) {
                    case TransactionStatus.DECLINE:
                        balanceStatus = BalanceStatus.FAILED
                        break
                    case TransactionStatus.SUCCESS:
                        switch (transactionType) {
                            case TransactionType.AUTHORIZE:
                                balanceStatus = BalanceStatus.PENDING_CAPTURE
                                break
                            case TransactionType.CAPTURE:
                            case TransactionType.CHARGE:
                                balanceStatus = BalanceStatus.AWAITING_PAYMENT
                                break
                            case TransactionType.REVERSE:
                                balanceStatus = BalanceStatus.CANCELLED
                                break
                            default:
                                balanceStatus = BalanceStatus.ERROR
                                break
                        }
                        break
                    default:
                        balanceStatus = BalanceStatus.ERROR
                        break
                }
                balance.setStatus(balanceStatus.name())
            }
        } else {
            transaction.setStatus(TransactionStatus.ERROR.name())
        }
        balance.addTransaction(transaction)
        return balance
    }

    private PaymentTransaction generatePaymentTransaction(Balance balance) {
        def paymentTransaction = new PaymentTransaction()
        paymentTransaction.setTrackingUuid(UUID.randomUUID())
        paymentTransaction.setPaymentInstrumentId(balance.piId.value)

        def chargeInfo = new ChargeInfo()
        chargeInfo.setCurrency(balance.currency)
        chargeInfo.setAmount(balance.totalAmount)
        chargeInfo.setCountry(balance.country)
        paymentTransaction.setChargeInfo(chargeInfo)

        return paymentTransaction
    }

}
