/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.core.service

import com.junbo.billing.clientproxy.PaymentFacade
import com.junbo.billing.db.repository.TransactionRepository
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
    void processBalance(Balance balance) {

        def paymentTransaction = generatePaymentTransaction(balance)
        final TXN_TYPE
        Promise<PaymentTransaction> promiseResponse = null
        BalanceType balanceType = BalanceType.valueOf(balance.type)
        switch (balanceType) {
            case BalanceType.DEBIT:
                promiseResponse = paymentFacade.postPaymentCharge(paymentTransaction)
                TXN_TYPE = TransactionType.CHARGE
                break
            case BalanceType.DELAY_DEBIT:
                promiseResponse = paymentFacade.postPaymentAuthorization(paymentTransaction)
                TXN_TYPE = TransactionType.AUTHORIZE
                break
            default:
                throw AppErrors.INSTANCE.invalidBalanceType(balance.type).exception()

        }
        promiseResponse?.then(new Promise.Func<PaymentTransaction, Promise>() {
            @Override
            Promise apply(PaymentTransaction responsePaymentTransaction) {

                def transaction = new Transaction()
                transaction.setBalanceId(balance.balanceId)
                transaction.setAmount(responsePaymentTransaction.chargeInfo.amount)
                transaction.setCurrency(responsePaymentTransaction.chargeInfo.currency)
                transaction.setPiId(new PaymentInstrumentId(responsePaymentTransaction.paymentInstrumentId))
                transaction.setTransactionType(TXN_TYPE.name())

                transaction.setPaymentRefId(responsePaymentTransaction.paymentId.toString())

                PaymentStatus paymentStatus = PaymentStatus.valueOf(responsePaymentTransaction.status)
                switch (paymentStatus) {
                    case PaymentStatus.AUTHORIZED:
                    case PaymentStatus.SETTLEMENT_SUBMITTED:
                    case PaymentStatus.REVERSED:
                        transaction.setTransactonStatus(TransactionStatus.SUCCESS.name())
                        break
                    case PaymentStatus.AUTH_DECLINED:
                    case PaymentStatus.REVERSE_DECLINED:
                    case PaymentStatus.SETTLEMENT_DECLINED:
                        transaction.setTransactonStatus(TransactionStatus.DECLINE.name())
                        break
                    default:
                        transaction.setTransactonStatus(TransactionStatus.ERROR.name())
                        break
                }
                transactionRepository.saveTransaction(transaction)
            }
        } )


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
