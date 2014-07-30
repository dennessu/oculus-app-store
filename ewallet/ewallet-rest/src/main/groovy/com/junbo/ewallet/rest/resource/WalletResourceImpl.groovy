/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.ewallet.rest.resource

import com.junbo.common.id.UserId
import com.junbo.common.id.WalletId
import com.junbo.common.model.Results
import com.junbo.ewallet.service.WalletService
import com.junbo.ewallet.spec.model.*
import com.junbo.ewallet.spec.resource.WalletResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired

/**
 * WalletResource Impl.
 */
@CompileStatic
class WalletResourceImpl implements WalletResource {
    @Autowired
    private WalletService walletService

    @Override
    Promise<Wallet> getWallet(WalletId walletId) {
        Wallet result = walletService.get(walletId.value)
        return Promise.pure(result)
    }

    @Override
    Promise<Results<Wallet>> getWallets(UserId userId) {
        List<Wallet> wallets = walletService.getWallets(userId)
        Results<Wallet> result = new Results<>()
        result.items = wallets
        return Promise.pure(result)
    }

    @Override
    Promise<Wallet> postWallet(Wallet wallet) {
        Wallet existed = getByTrackingUuid(wallet.userId, wallet.trackingUuid)
        if (existed != null) {
            return Promise.pure(existed)
        }
        Wallet result = walletService.add(wallet)
        return Promise.pure(result)
    }

    @Override
    Promise<Wallet> updateWallet(WalletId walletId, Wallet wallet) {
        Wallet existed = getByTrackingUuid(wallet.userId, wallet.trackingUuid)
        if (existed != null) {
            return Promise.pure(existed)
        }
        Wallet result = walletService.update(walletId.value, wallet)
        return Promise.pure(result)
    }

    @Override
    Promise<Transaction> credit(CreditRequest creditRequest) {
        Transaction existed = getTransactionByTrackingUuid(creditRequest.userId, creditRequest.trackingUuid)
        if (existed != null) {
            return Promise.pure(existed)
        }
        Transaction result = walletService.credit(creditRequest)
        return Promise.pure(result)
    }

    @Override
    Promise<Transaction> debit(WalletId walletId, DebitRequest debitRequest) {
        Transaction existed = getTransactionByTrackingUuid(walletId.value, debitRequest.trackingUuid)
        if (existed != null) {
            return Promise.pure(existed)
        }
        Transaction result = walletService.debit(walletId.value, debitRequest)
        return Promise.pure(result)
    }

    @Override
    Promise<Transaction> refund(Long transactionId, RefundRequest refundRequest) {
        Transaction existed = getTransactionByTrackingUuid(transactionId, refundRequest.trackingUuid)
        if (existed != null) {
            return Promise.pure(existed)
        }
        Transaction result = walletService.refund(transactionId, refundRequest)
        return Promise.pure(result)
    }

    @Override
    Promise<Results<Transaction>> getTransactions(WalletId walletId) {
        List<Transaction> transactions = walletService.getTransactions(walletId.value)
        Results<Transaction> results = new Results<>()
        results.setItems(transactions)
        return Promise.pure(results)
    }

    private Wallet getByTrackingUuid(Long shardMasterId, UUID trackingUuid) {
        if (trackingUuid == null) {
            return null
        }
        return walletService.getWalletByTrackingUuid(shardMasterId, trackingUuid)
    }

    private Transaction getTransactionByTrackingUuid(Long shardMasterId, UUID trackingUuid) {
        if (trackingUuid == null) {
            return null
        }
        return walletService.getTransactionByTrackingUuid(shardMasterId, trackingUuid)
    }
}
