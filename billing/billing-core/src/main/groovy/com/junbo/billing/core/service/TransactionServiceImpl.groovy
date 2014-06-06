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
import com.junbo.billing.spec.enums.PropertyKey
import com.junbo.billing.spec.enums.TransactionStatus
import com.junbo.billing.spec.enums.TransactionType
import com.junbo.billing.spec.error.AppErrors
import com.junbo.billing.spec.model.Balance
import com.junbo.billing.spec.model.Transaction
import com.junbo.common.error.AppErrorException
import com.junbo.langur.core.promise.Promise
import com.junbo.payment.spec.enums.PaymentStatus
import com.junbo.payment.spec.model.ChargeInfo
import com.junbo.payment.spec.model.Item
import com.junbo.payment.spec.model.PaymentTransaction
import com.junbo.payment.spec.model.WebPaymentInfo
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.transaction.annotation.Transactional

/**
 * Created by xmchen on 14-2-19.
 */
@CompileStatic
@Transactional
class TransactionServiceImpl implements TransactionService {
    @Autowired
    TransactionRepository transactionRepository

    PaymentFacade paymentFacade

    @Autowired
    void setPaymentFacade(@Qualifier('billingPaymentFacade')PaymentFacade paymentFacade) {
        this.paymentFacade = paymentFacade
    }

    final static Long UNCONFIRMED_TIMEOUT_HOURS = 3 * 60 * 60 * 1000L

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionServiceImpl)

    @Override
    Promise<Balance> processBalance(Balance balance, Balance originalBalance) {

        BalanceType balanceType = BalanceType.valueOf(balance.type)
        switch (balanceType) {
            case BalanceType.DEBIT:
                return charge(balance)
            case BalanceType.MANUAL_CAPTURE:
                return authorize(balance)
            case BalanceType.REFUND:
                return refund(balance, originalBalance)
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
        Long paymentId = getPaymentIdByRef(balance.transactions[0].paymentRefId)

        def newTransaction = new Transaction()
        newTransaction.setAmount(amount)
        newTransaction.setCurrency(balance.currency)
        newTransaction.setType(TransactionType.CAPTURE.name())
        newTransaction.setPiId(balance.piId)
        newTransaction.setTransactionTime(new Date())

        LOGGER.info('name=Capture_Balance. balance currency: {}, authed amount: {}, settled amount: {}, pi id: {}',
                balance.currency, balance.totalAmount, amount, balance.piId)
        return paymentFacade.postPaymentCapture(paymentId, paymentTransaction).recover { Throwable throwable ->
            LOGGER.error('name=Capture_Balance_Error. error in post payment capture', throwable)
            newTransaction.setStatus(TransactionStatus.ERROR.name())
            balance.addTransaction(newTransaction)
            balance.setStatus(BalanceStatus.FAILED.name())

            if (throwable instanceof AppErrorException) {
                throw AppErrors.INSTANCE.paymentProcessingFailed(balance.piId.value.toString()).exception()
            }
            throw throwable
        }.then { PaymentTransaction pt ->
            LOGGER.info('name=Capture_Balance_Response. payment id: {}, amount: {}, status: {}',
                    pt.id, pt.chargeInfo.amount, pt.status)
            newTransaction.setPaymentRefId(pt.id.toString())
            newTransaction.setAmount(pt.chargeInfo.amount)
            newTransaction.setStatus(getTransactionStatusByPaymentStatus(pt.status).name())
            balance.setStatus(getBalanceStatusByPaymentStatus(pt.status).name())

            balance.addTransaction(newTransaction)
            return Promise.pure(balance)
        }
    }

    @Override
    Promise<Balance> confirmBalance(Balance balance) {

        return checkBalance(balance).then {

            Long paymentId = getPaymentIdByRef(balance.transactions[0].paymentRefId)

            def newTransaction = new Transaction()
            newTransaction.setAmount(balance.totalAmount)
            newTransaction.setCurrency(balance.currency)
            newTransaction.setType(TransactionType.CONFIRM.name())
            newTransaction.setPiId(balance.piId)
            newTransaction.setTransactionTime(new Date())

            def paymentTransaction = generatePaymentTransaction(balance)

            LOGGER.info('name=Confirm_Balance. balance currency: {}, amount: {}, pi id: {}',
                    balance.currency, balance.totalAmount, balance.piId)
            return paymentFacade.postPaymentConfirm(paymentId, paymentTransaction).recover { Throwable throwable ->
                LOGGER.error('name=Confirm_Balance_Error. error in post payment confirm', throwable)
                newTransaction.setStatus(TransactionStatus.ERROR.name())
                balance.addTransaction(newTransaction)

                if (throwable instanceof AppErrorException) {
                    throw AppErrors.INSTANCE.paymentProcessingFailed(balance.piId.value.toString()).exception()
                }
                throw throwable
            }.then { PaymentTransaction pt ->
                LOGGER.info('name=Confirm_Balance_Response. payment id: {}, amount: {}, status: {}',
                        pt.id, pt.chargeInfo.amount, pt.status)
                newTransaction.setPaymentRefId(pt.id.toString())
                newTransaction.setAmount(pt.chargeInfo.amount)
                newTransaction.setStatus(getTransactionStatusByPaymentStatus(pt.status).name())
                balance.setStatus(getBalanceStatusByPaymentStatus(pt.status).name())

                balance.addTransaction(newTransaction)
                return Promise.pure(balance)
            }
        }
    }
    @Override
    Promise<Balance> checkBalance(Balance balance) {

        Long paymentId = getPaymentIdByRef(balance.transactions[0].paymentRefId)

        def newTransaction = new Transaction()
        newTransaction.setAmount(balance.totalAmount)
        newTransaction.setCurrency(balance.currency)
        newTransaction.setType(TransactionType.CHECK.name())
        newTransaction.setPiId(balance.piId)
        newTransaction.setTransactionTime(new Date())

        return paymentFacade.postPaymentCheck(paymentId).recover { Throwable throwable ->
            LOGGER.error('name=Check_Balance_Error. error in get payment', throwable)
            newTransaction.setStatus(TransactionStatus.ERROR.name())
            balance.addTransaction(newTransaction)
            balance.setStatus(BalanceStatus.ERROR.name())
            throw throwable
        }.then { PaymentTransaction checkPt ->
            if (checkPt == null) {
                throw AppErrors.INSTANCE.invalidPaymentId(paymentId.toString()).exception()
            }

            LOGGER.info('name=Check_Balance_Get_Payment. payment id: {}, amount: {}, status: {}',
                    checkPt.id, checkPt.chargeInfo.amount, checkPt.status)

            if (checkPt.id != null) {
                newTransaction.setPaymentRefId(checkPt.id.toString())
            }

            String newStatus = getBalanceStatusByPaymentStatus(checkPt.status).name()
            if (balance.status != newStatus) {
                balance.setStatus(newStatus)
                if (checkPt.status != PaymentStatus.UNCONFIRMED) {
                    newTransaction.setStatus(getTransactionStatusByPaymentStatus(checkPt.status).name())
                    balance.addTransaction(newTransaction)
                    return Promise.pure(balance)
                }

                if (isTimeLimitReached(balance.transactions[0].transactionTime)) {
                    LOGGER.info('name=Confirm_Balance_Timeout. ')
                    newTransaction.setStatus(TransactionStatus.TIMEOUT.name())
                    balance.addTransaction(newTransaction)
                    balance.setStatus(BalanceStatus.FAILED.name())
                    return Promise.pure(balance)
                }
            } else {
                LOGGER.info('name=Check_Balance_Get_Payment. There is no new status update.')
                return Promise.pure(balance)
            }
        }
    }

    private Promise<Balance> charge(Balance balance) {
        def transaction = new Transaction()
        transaction.setAmount(balance.totalAmount)
        transaction.setCurrency(balance.currency)
        transaction.setType(TransactionType.CHARGE.name())
        transaction.setPiId(balance.piId)
        transaction.setTransactionTime(new Date())

        def paymentTransaction = generatePaymentTransaction(balance)
        LOGGER.info('name=Charge_Balance. balance currency: {}, amount: {}, pi id: {}',
                balance.currency, balance.totalAmount, balance.piId)
        return paymentFacade.postPaymentCharge(paymentTransaction).recover { Throwable throwable ->
            LOGGER.error('name=Charge_Balance_Error. error in post payment charge', throwable)
            transaction.setStatus(TransactionStatus.ERROR.name())
            balance.addTransaction(transaction)
            balance.setStatus(BalanceStatus.FAILED.name())

            if (throwable instanceof AppErrorException) {
                // check whether is insufficient fund
                def appException = ((AppErrorException)throwable)
                if (appException.error.code == '40028') {
                    throw AppErrors.INSTANCE.paymentInsufficientFund(balance.piId.value.toString()).exception()
                }
                throw AppErrors.INSTANCE.paymentProcessingFailed(balance.piId.value.toString()).exception()
            }
            throw throwable
        }.then { PaymentTransaction pt ->
            LOGGER.info('name=Charge_Balance. payment id: {}, status: {}', pt.id, pt.status)
            transaction.setPaymentRefId(pt.id.toString())
            transaction.setStatus(getTransactionStatusByPaymentStatus(pt.status).name())
            balance.setStatus(getBalanceStatusByPaymentStatus(pt.status).name())
            if (pt.status == PaymentStatus.UNCONFIRMED.name() && pt.webPaymentInfo != null) {
                balance.setProviderConfirmUrl(pt.webPaymentInfo.redirectURL)
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
        transaction.setTransactionTime(new Date())

        def paymentTransaction = generatePaymentTransaction(balance)
        LOGGER.info('name=Authorize_Balance. balance currency: {}, amount: {}, pi id: {}',
                balance.currency, balance.totalAmount, balance.piId)
        return paymentFacade.postPaymentAuthorization(paymentTransaction).recover { Throwable throwable ->
            LOGGER.error('name=Authorize_Balance_Error. error in post payment authorization', throwable)
            transaction.setStatus(TransactionStatus.ERROR.name())
            balance.addTransaction(transaction)
            balance.setStatus(BalanceStatus.FAILED.name())

            if (throwable instanceof AppErrorException) {
                throw AppErrors.INSTANCE.paymentProcessingFailed(balance.piId.value.toString()).exception()
            }
            throw throwable
        }.then { PaymentTransaction pt ->
            LOGGER.info('name=Authorize_Balance. payment id: {}, status: {}', pt.id, pt.status)
            transaction.setPaymentRefId(pt.id.toString())
            transaction.setStatus(getTransactionStatusByPaymentStatus(pt.status).name())
            balance.setStatus(getBalanceStatusByPaymentStatus(pt.status).name())

            balance.addTransaction(transaction)
            return Promise.pure(balance)
        }
    }

    private Promise<Balance> refund(Balance balance, Balance originalBalance) {

        def transaction = new Transaction()
        transaction.setAmount(balance.totalAmount)
        transaction.setCurrency(balance.currency)
        transaction.setType(TransactionType.REFUND.name())
        transaction.setPiId(balance.piId)
        transaction.setTransactionTime(new Date())

        def paymentTransaction = generatePaymentTransaction(balance)
        Long paymentId = getPaymentIdByRef(originalBalance.transactions[0].paymentRefId)
        LOGGER.info('name=Refund_Balance. balance currency: {}, amount: {}, pi id: {}',
                balance.currency, balance.totalAmount, balance.piId)
        return paymentFacade.postPaymentRefund(paymentId, paymentTransaction).recover { Throwable throwable ->
            LOGGER.error('name=Refund_Balance_Error. error in post payment refund', throwable)
            transaction.setStatus(TransactionStatus.ERROR.name())
            balance.addTransaction(transaction)
            balance.setStatus(BalanceStatus.FAILED.name())

            if (throwable instanceof AppErrorException) {
                throw AppErrors.INSTANCE.paymentProcessingFailed(balance.piId.value.toString()).exception()
            }
            throw throwable
        }.then { PaymentTransaction pt ->
            LOGGER.info('name=Refund_Balance. payment id: {}, status: {}', pt.id, pt.status)
            transaction.setPaymentRefId(pt.id.toString())
            transaction.setStatus(getTransactionStatusByPaymentStatus(pt.status).name())
            balance.setStatus(getBalanceStatusByPaymentStatus(pt.status).name())

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

        String description = balance.getProperty(PropertyKey.BALANCE_DESCRIPTION)
        if (description != null) {
            Item paymentItem = new Item()
            paymentItem.name = description
            paymentItem.amount = balance.totalAmount
            paymentItem.quantity = 1
            chargeInfo.setItems([ paymentItem ])
        }

        def webPaymentInfo = new WebPaymentInfo()
        if (balance.successRedirectUrl != null || balance.cancelRedirectUrl != null) {
            webPaymentInfo.setReturnURL(balance.successRedirectUrl)
            webPaymentInfo.setCancelURL(balance.cancelRedirectUrl)
        }
        paymentTransaction.setWebPaymentInfo(webPaymentInfo)

        return paymentTransaction
    }

    private Long getPaymentIdByRef(String refId) {
        String paymentRefId = refId
        Long paymentId
        try {
            paymentId = Long.parseLong(paymentRefId)
        } catch (NumberFormatException ex) {
            throw AppErrors.INSTANCE.invalidPaymentId(paymentRefId).exception()
        }
        return paymentId
    }

    private TransactionStatus getTransactionStatusByPaymentStatus(String paymentStatus) {
        switch (paymentStatus) {
            case PaymentStatus.AUTHORIZED.name():
            case PaymentStatus.SETTLEMENT_SUBMITTED.name():
            case PaymentStatus.SETTLING.name():
            case PaymentStatus.SETTLED.name():
            case PaymentStatus.REFUNDED.name():
                return TransactionStatus.SUCCESS
            case PaymentStatus.SETTLEMENT_SUBMIT_DECLINED.name():
            case PaymentStatus.AUTH_DECLINED.name():
            case PaymentStatus.REFUND_DECLINED.name():
                return TransactionStatus.DECLINE
            case PaymentStatus.UNCONFIRMED.name():
                return TransactionStatus.UNCONFIRMED
            default:
                return TransactionStatus.ERROR
        }
    }

    private BalanceStatus getBalanceStatusByPaymentStatus(String paymentStatus) {
        switch (paymentStatus) {
            case PaymentStatus.SETTLEMENT_SUBMITTED.name():
            case PaymentStatus.SETTLING.name():
                return BalanceStatus.AWAITING_PAYMENT
            case PaymentStatus.SETTLEMENT_SUBMIT_DECLINED.name():
            case PaymentStatus.AUTH_DECLINED.name():
            case PaymentStatus.REFUND_DECLINED.name():
                return BalanceStatus.FAILED
            case PaymentStatus.UNCONFIRMED.name():
                return BalanceStatus.UNCONFIRMED
            case PaymentStatus.SETTLED.name():
            case PaymentStatus.REFUNDED.name():
                return BalanceStatus.COMPLETED
            case PaymentStatus.AUTHORIZED.name():
                return BalanceStatus.PENDING_CAPTURE
            default:
                return BalanceStatus.ERROR
        }
    }

    private Boolean isTimeLimitReached(Date referenceDate) {
        return System.currentTimeMillis() - referenceDate.time >= UNCONFIRMED_TIMEOUT_HOURS
    }
}
