package com.junbo.ewallet.service.impl

import com.junbo.ewallet.db.entity.def.NotEnoughMoneyException
import com.junbo.ewallet.db.repo.WalletRepository
import com.junbo.ewallet.service.WalletService
import com.junbo.ewallet.spec.def.Status
import com.junbo.ewallet.spec.def.WalletLotType
import com.junbo.ewallet.spec.def.WalletType
import com.junbo.ewallet.spec.error.AppErrors
import com.junbo.ewallet.spec.model.CreditRequest
import com.junbo.ewallet.spec.model.DebitRequest
import com.junbo.ewallet.spec.model.Transaction
import com.junbo.ewallet.spec.model.Wallet
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.CollectionUtils

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
    @Autowired
    private WalletRepository walletRepo

    @Override
    @Transactional
    Wallet get(Long walletId) {
        Wallet result = walletRepo.get(walletId)
        if (result == null) {
            throw AppErrors.INSTANCE.notFound('wallet', walletId).exception()
        }
        checkUserId(result.userId)
        return result
    }

    @Override
    @Transactional
    Wallet add(Wallet wallet) {
        if (wallet.walletId != null) {
            throw AppErrors.INSTANCE.unnecessaryField('id').exception()
        }

        checkUserId(wallet.userId)

        if (wallet.status != null) {
            throw AppErrors.INSTANCE.unnecessaryField('status').exception()
        }
        wallet.status = Status.ACTIVE.toString()

        if (wallet.type == null) {
            throw AppErrors.INSTANCE.missingField('type').exception()
        } else if (wallet.type.equalsIgnoreCase(WalletType.STORED_VALUE.toString()) && wallet.currency == null) {
            throw AppErrors.INSTANCE.missingField('currency').exception()
        }

        if (wallet.balance == null) {
            wallet.balance = BigDecimal.ZERO
        } else if (!wallet.balance == BigDecimal.ZERO) {
            throw AppErrors.INSTANCE.fieldNotCorrect('balance', 'balance should be 0.').exception()
        }

        if (!CollectionUtils.isEmpty(wallet.transactions)) {
            throw AppErrors.INSTANCE.unnecessaryField('transactions').exception()
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
        validateNotNull(wallet.walletId, 'id')
        validateEquals(wallet.walletId, walletId, 'id')

        Wallet existed = get(walletId)

        validateEquals(wallet.type, existed.type, 'type')
        validateEquals(wallet.userId, existed.userId, 'user')
        validateEquals(wallet.currency, existed.currency, 'currency')
        validateEquals(wallet.balance, existed.balance, 'balance')

        wallet.walletId = walletId
        Wallet result = walletRepo.update(wallet)
        return result
    }

    @Override
    @Transactional
    Wallet credit(CreditRequest creditRequest) {
        if (creditRequest.type == null) {
            creditRequest.type = WalletLotType.CASH.toString()
        }

        validateAmount(creditRequest.amount)
        validateExpirationDate(creditRequest.expirationDate)

        def wallet
        if (creditRequest.walletId != null) {
            wallet = walletRepo.get(creditRequest.walletId)
        } else if (creditRequest.userId != null && creditRequest.currency != null) {
            wallet = walletRepo.get(creditRequest.userId, creditRequest.type, creditRequest.currency)
        } else {
            throw AppErrors.INSTANCE.common('wallet or user and currency should not be null').exception()
        }

        if (wallet == null) {
            throw AppErrors.INSTANCE.common('wallet not found').exception()
        }
        checkUserId(wallet.userId)
        if (wallet.status.equalsIgnoreCase(Status.LOCKED.toString())) {
            throw AppErrors.INSTANCE.locked(wallet.walletId).exception()
        }

        Wallet result = walletRepo.credit(wallet, creditRequest)
        return result
    }

    @Override
    @Transactional
    Wallet debit(Long walletId, DebitRequest debitRequest) {
        validateAmount(debitRequest.amount)

        Wallet wallet = get(walletId)
        if (wallet.status.equalsIgnoreCase(Status.LOCKED.toString())) {
            throw AppErrors.INSTANCE.locked(walletId).exception()
        }
        if (wallet.balance < debitRequest.amount) {
            throw AppErrors.INSTANCE.notEnoughMoney(walletId).exception()
        }

        Wallet result
        try {
            result = walletRepo.debit(wallet, debitRequest)
        } catch (NotEnoughMoneyException e) {
            throw AppErrors.INSTANCE.notEnoughMoney(walletId).exception()
        }

        return result
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null) {
            throw AppErrors.INSTANCE.missingField('amount').exception()
        } else if (amount <= 0) {
            throw AppErrors.INSTANCE.fieldNotCorrect('amount', 'Amount should be positive.').exception()
        }
    }

    private void validateExpirationDate(Date expirationDate) {
        if (expirationDate != null) {
            Date now = new Date()
            if (expirationDate.before(now)) {
                throw AppErrors.INSTANCE.fieldNotCorrect(
                        'expirationDate', 'expirationDate should not be before now.')
                        .exception()
            }
        }
    }

    @Override
    @Transactional
    Wallet getTransactions(Long walletId) {
        Wallet result = get(walletId)
        List<Transaction> transactions = walletRepo.getTransactions(walletId)
        result.transactions = transactions
        return result
    }

    @Override
    @Transactional
    Wallet getByTrackingUuid(Long shardMasterId, UUID trackingUuid) {
        return walletRepo.getByTrackingUuid(shardMasterId, trackingUuid)
    }

    private void checkUserId(Long userId) {
        if (userId == null) {
            throw AppErrors.INSTANCE.missingField('userId').exception()
        }
    }

    private void validateNotNull(Object o, String fieldName) {
        if (o == null) {
            throw AppErrors.INSTANCE.missingField(fieldName).exception()
        }
    }

    private void validateEquals(Object actual, Object expected, String fieldName) {
        if (expected == actual) {
            return
        } else if (expected == null || actual == null) {
            throw AppErrors.INSTANCE.fieldNotMatch(fieldName, actual, expected).exception()
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
            throw AppErrors.INSTANCE.fieldNotMatch(fieldName, actual, expected).exception()
        }
    }
}