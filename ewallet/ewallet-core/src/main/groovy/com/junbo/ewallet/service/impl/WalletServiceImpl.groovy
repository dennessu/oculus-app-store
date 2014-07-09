package com.junbo.ewallet.service.impl

import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.UserId
import com.junbo.common.id.WalletId
import com.junbo.ewallet.common.util.Callback
import com.junbo.ewallet.db.entity.def.NotEnoughMoneyException
import com.junbo.ewallet.db.repo.facade.TransactionRepositoryFacade
import com.junbo.ewallet.db.repo.facade.WalletRepositoryFacade
import com.junbo.ewallet.service.WalletService
import com.junbo.ewallet.spec.def.Status
import com.junbo.ewallet.spec.def.WalletLotType
import com.junbo.ewallet.spec.def.WalletType
import com.junbo.ewallet.spec.error.AppErrors
import com.junbo.ewallet.spec.model.*
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.Transactional

/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

/**
 * Impl of WalletService.
 */
@CompileStatic
class WalletServiceImpl implements WalletService {
    private static final Logger LOGGER = LoggerFactory.getLogger(WalletService.class)

    @Autowired
    private ApplicationContext applicationContext
    @Autowired
    private WalletRepositoryFacade walletRepo
    @Autowired
    private TransactionRepositoryFacade transactionRepo
    @Autowired
    private PlatformTransactionManager transactionManager
    @Autowired
    private TransactionSupport transactionSupport

    @Override
    @Transactional
    Wallet get(Long walletId) {
        Wallet result = walletRepo.get(walletId)
        if (result == null) {
            throw AppCommonErrors.INSTANCE.resourceNotFound('wallet', new WalletId(walletId)).exception()
        }
        checkUserId(result.userId)
        return result
    }

    @Override
    @Transactional
    Wallet add(Wallet wallet) {
        if (wallet.getId() != null) {
            throw AppCommonErrors.INSTANCE.fieldMustBeNull('id').exception()
        }

        checkUserId(wallet.userId)

        if (wallet.status != null) {
            throw AppCommonErrors.INSTANCE.fieldMustBeNull('status').exception()
        }
        wallet.status = Status.ACTIVE.toString()

        if (wallet.type != null &&
                !wallet.type.equalsIgnoreCase(WalletType.STORED_VALUE.toString())) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('type', 'only STORED_VALUE supported').exception()
        }
        wallet.type = WalletType.STORED_VALUE.toString()

        if (wallet.type == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('type').exception()
        } else if (wallet.type.equalsIgnoreCase(WalletType.STORED_VALUE.toString()) && wallet.currency == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('currency').exception()
        }

        wallet.currency = wallet.currency.toUpperCase()

        if (wallet.balance == null) {
            wallet.balance = BigDecimal.ZERO
        } else if (!wallet.balance == BigDecimal.ZERO) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('balance', 'balance should be 0.').exception()
        }

        Wallet existed = walletRepo.get(wallet.userId, wallet.type, wallet.currency)
        if (existed != null) {
            return existed
        }

        Wallet result = walletRepo.create(wallet)
        return result
    }

    @Override
    @Transactional
    Wallet update(Long walletId, Wallet wallet) {
        validateNotNull(wallet.getId(), 'id')
        validateEquals(wallet.getId(), walletId, 'id')

        Wallet existed = get(walletId)

        validateEquals(wallet.type, existed.type, 'type')
        validateEquals(wallet.userId, existed.userId, 'user')
        validateEquals(wallet.currency, existed.currency, 'currency')
        validateEquals(wallet.balance, existed.balance, 'balance')

        wallet.setId(walletId)
        Wallet result = walletRepo.update(wallet)
        return result
    }

    @Override
    @Transactional
    Transaction credit(CreditRequest creditRequest) {
        if (creditRequest.creditType == null) {
            creditRequest.creditType = WalletLotType.CASH.toString()
        }
        if (!creditRequest.creditType.equalsIgnoreCase(WalletLotType.CASH.toString())) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('creditType', 'ony CASH supported').exception()
        }

        if (creditRequest.walletType == null) {
            creditRequest.walletType = WalletType.STORED_VALUE.toString()
        }
        if (!creditRequest.walletType.equalsIgnoreCase(WalletType.STORED_VALUE.toString())) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('walletType', 'ony STORED_VALUE supported').exception()
        }

        validateAmount(creditRequest.amount)
        validateExpirationDate(creditRequest.expirationDate)

        def wallet
        if (creditRequest.walletId != null) {
            wallet = walletRepo.get(creditRequest.walletId)
        } else if (creditRequest.userId != null &&
                creditRequest.currency != null &&
                creditRequest.walletType != null) {
            wallet = walletRepo.get(creditRequest.userId, creditRequest.walletType, creditRequest.currency.toUpperCase())
        } else {
            throw AppCommonErrors.INSTANCE.invalidOperation('Either wallet or (user, currency, walletType) should not specified').exception()
        }

        if (wallet == null) {
            throw AppCommonErrors.INSTANCE.resourceNotFound("wallet", creditRequest.walletId).exception()
        }
        checkUserId(wallet.userId)
        if (wallet.status.equalsIgnoreCase(Status.LOCKED.toString())) {
            throw AppErrors.INSTANCE.locked().exception()
        }

        Transaction result = walletRepo.credit(wallet, creditRequest)
        return result
    }

    @Override
    Transaction debit(Long walletId, DebitRequest debitRequest) {
        validateAmount(debitRequest.amount)

        Wallet wallet = ((WalletService) applicationContext.getBean("walletService")).get(walletId)
        if (wallet == null) {
            throw AppCommonErrors.INSTANCE.resourceNotFound("wallet", walletId).exception()
        }
        checkUserId(wallet.userId)
        if (wallet.status.equalsIgnoreCase(Status.LOCKED.toString())) {
            throw AppErrors.INSTANCE.locked().exception()
        }
        if (wallet.balance < debitRequest.amount) {
            throw AppErrors.INSTANCE.insufficientFund().exception()
        }

        Transaction result
        try {
            transactionSupport.executeInNewTransaction(new Callback() {
                @Override
                void apply() {
                    result = walletRepo.debit(wallet, debitRequest)
                }
            })

        } catch (NotEnoughMoneyException e) {
            LOGGER.error("There is not enough money in wallet [$walletId]" +
                    " because some of the walletLots have expired.")
            //correct the wallet balance
            transactionSupport.executeInNewTransaction(new Callback() {
                @Override
                void apply() {
                    LOGGER.info("Correct the balance in wallet [$walletId].")
                    walletRepo.correctBalance(wallet.getId())
                }
            })
            throw AppErrors.INSTANCE.insufficientFund().exception()
        }

        return result
    }

    @Override
    @Transactional
    List<Transaction> getTransactions(Long walletId) {
        List<Transaction> transactions = transactionRepo.getTransactions(walletId)
        return transactions
    }

    @Override
    @Transactional
    Wallet getWalletByTrackingUuid(Long shardMasterId, UUID trackingUuid) {
        return walletRepo.getByTrackingUuid(shardMasterId, trackingUuid)
    }

    @Override
    @Transactional
    Transaction getTransactionByTrackingUuid(Long shardMasterId, UUID trackingUuid) {
        return transactionRepo.getByTrackingUuid(shardMasterId, trackingUuid)
    }

    @Override
    @Transactional
    List<Wallet> getWallets(UserId userId) {
        validateNotNull(userId, "userId")
        checkUserId(userId.value)
        return walletRepo.getAll(userId.value)
    }

    @Override
    @Transactional
    Transaction refund(Long transactionId, RefundRequest refundRequest) {
        validateAmount(refundRequest.amount)

        Transaction debitTransaction = transactionRepo.get(transactionId)
        if (debitTransaction == null) {
            throw AppCommonErrors.INSTANCE.fieldInvalid("transactionId",
                    "transaction [" + transactionId + "] not found").exception()
        }
        if (!debitTransaction.getType().equals("DEBIT")) {
            throw AppCommonErrors.INSTANCE.fieldInvalid("transactionId",
                    "transaction with type [" + debitTransaction.getType() + " ] not supported").exception()
        }
        if (debitTransaction.unrefundedAmount.compareTo(refundRequest.amount) < 0) {
            throw AppCommonErrors.INSTANCE.fieldInvalid("amount",
                    "there is not enough money to refund").exception()
        }

        Wallet wallet = get(debitTransaction.getWalletId())
        if (wallet == null) {
            throw AppCommonErrors.INSTANCE.resourceNotFound("wallet", debitTransaction.getWalletId()).exception()
        }
        checkUserId(wallet.userId)
        validateEquals(refundRequest.currency, wallet.currency, "currency")
        if (wallet.status.equalsIgnoreCase(Status.LOCKED.toString())) {
            throw AppErrors.INSTANCE.locked().exception()
        }

        return walletRepo.refund(wallet, transactionId, refundRequest)
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('amount').exception()
        } else if (amount <= 0) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('amount', 'Amount should be positive.').exception()
        }
    }

    private void validateExpirationDate(Date expirationDate) {
        if (expirationDate != null) {
            Date now = new Date()
            if (expirationDate.before(now)) {
                throw AppCommonErrors.INSTANCE.fieldInvalid(
                        'expirationDate', 'expirationDate should not be before now.')
                        .exception()
            }
        }
    }

    private void checkUserId(Long userId) {
        if (userId == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('userId').exception()
        }
    }

    private void validateNotNull(Object o, String fieldName) {
        if (o == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired(fieldName).exception()
        }
    }

    private void validateEquals(Object actual, Object expected, String fieldName) {
        if (expected == actual) {
            return
        } else if (expected == null || actual == null) {
            throw AppCommonErrors.INSTANCE.fieldNotWritable(fieldName, actual, expected).exception()
        }
        Boolean equals = true
        if (actual instanceof String) {
            if (!((String) expected).equalsIgnoreCase((String) actual)) {
                equals = false
            }
        } else if (actual instanceof Date) {
            if (Math.abs(((Date) actual).time - ((Date) expected).time) > 1000) {
                equals = false
            }
        } else if (expected != actual) {
            equals = false
        }

        if (!equals) {
            throw AppCommonErrors.INSTANCE.fieldNotWritable(fieldName, actual, expected).exception()
        }
    }
}