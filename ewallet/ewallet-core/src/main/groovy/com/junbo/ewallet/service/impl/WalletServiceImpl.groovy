package com.junbo.ewallet.service.impl

import com.junbo.ewallet.db.entity.def.NotEnoughMoneyException
import com.junbo.ewallet.db.entity.def.Status
import com.junbo.ewallet.db.entity.def.WalletLotType
import com.junbo.ewallet.db.entity.def.WalletType
import com.junbo.ewallet.db.repo.WalletRepository
import com.junbo.ewallet.service.WalletService
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
        } else if (wallet.type.equalsIgnoreCase(WalletType.SV.toString()) && wallet.currency == null) {
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
        if (wallet.walletId == null) {
            throw AppErrors.INSTANCE.missingField('id').exception()
        }
        if (walletId != wallet.walletId) {
            throw AppErrors.INSTANCE.fieldNotMatch('id', wallet.walletId, walletId).exception()
        }

        Wallet existed = get(walletId)

        if (existed.type != wallet.type) {
            throw AppErrors.INSTANCE.fieldNotMatch('type', wallet.type, existed.type).exception()
        }
        if (existed.userId != wallet.userId) {
            throw AppErrors.INSTANCE.fieldNotMatch('userId', wallet.userId, existed.userId).exception()
        }
        if (existed.currency != wallet.currency) {
            throw AppErrors.INSTANCE.fieldNotMatch('currency', wallet.currency, existed.currency).exception()
        }
        if (existed.balance != wallet.balance) {
            throw AppErrors.INSTANCE.fieldNotMatch('balance', wallet.balance, existed.balance).exception()
        }

        wallet.walletId = walletId
        Wallet result = walletRepo.update(wallet)
        return result
    }

    @Override
    @Transactional
    Wallet credit(Long walletId, CreditRequest creditRequest) {
        if (creditRequest.type == null) {
            creditRequest.type = WalletLotType.CASH.toString()
        }
        if (creditRequest.amount == null) {
            throw AppErrors.INSTANCE.missingField('amount').exception()
        } else if (creditRequest.amount <= 0) {
            throw AppErrors.INSTANCE.fieldNotCorrect('amount', 'Amount should be positive.').exception()
        }

        Wallet wallet = get(walletId)
        if (wallet.status.equalsIgnoreCase(Status.LOCKED.toString())) {
            throw AppErrors.INSTANCE.locked(walletId).exception()
        }
        Wallet result = walletRepo.credit(wallet, creditRequest)
        return result
    }

    @Override
    @Transactional
    Wallet debit(Long walletId, DebitRequest debitRequest) {
        if (debitRequest.amount == null) {
            throw AppErrors.INSTANCE.missingField('amount').exception()
        } else if (debitRequest.amount <= 0) {
            throw AppErrors.INSTANCE.fieldNotCorrect('amount', 'Amount should be positive.').exception()
        }

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

    @Override
    @Transactional
    List<Transaction> getTransactions(Long walletId) {
        List<Transaction> result = walletRepo.getTransactions(walletId)
        return result
    }

    @Override
    @Transactional
    Wallet getByTrackingUuid(UUID trackingUuid) {
        return walletRepo.getByTrackingUuid(trackingUuid)
    }

    private void checkUserId(Long userId) {
        if (userId == null) {
            throw AppErrors.INSTANCE.missingField('userId').exception()
        }
    }
}
