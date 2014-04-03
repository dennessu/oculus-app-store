/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.ewallet.rest.resource
import com.junbo.common.id.WalletId
import com.junbo.ewallet.service.WalletService
import com.junbo.ewallet.spec.model.CreditRequest
import com.junbo.ewallet.spec.model.DebitRequest
import com.junbo.ewallet.spec.model.Wallet
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
    Promise<Wallet> postWallet(Wallet wallet) {
        Wallet existed = getByTrackingUuid(wallet.trackingUuid)
        if (existed != null) {
            return Promise.pure(existed)
        }
        Wallet result = walletService.add(wallet)
        return Promise.pure(result)
    }

    @Override
    Promise<Wallet> updateWallet(WalletId walletId, Wallet wallet) {
        Wallet existed = getByTrackingUuid(wallet.trackingUuid)
        if (existed != null) {
            return Promise.pure(existed)
        }
        Wallet result = walletService.update(walletId.value, wallet)
        return Promise.pure(result)
    }

    @Override
    Promise<Wallet> credit(CreditRequest creditRequest) {
        Wallet existed = getByTrackingUuid(creditRequest.trackingUuid)
        if (existed != null) {
            return Promise.pure(existed)
        }
        Wallet result = walletService.credit(creditRequest)
        return Promise.pure(result)
    }

    @Override
    Promise<Wallet> debit(WalletId walletId, DebitRequest debitRequest) {
        Wallet existed = getByTrackingUuid(debitRequest.trackingUuid)
        if (existed != null) {
            return Promise.pure(existed)
        }
        Wallet result = walletService.debit(walletId.value, debitRequest)
        return Promise.pure(result)
    }

    @Override
    Promise<Wallet> getTransactions(WalletId walletId) {
        return Promise.pure(walletService.getTransactions(walletId.value))
    }

    private Wallet getByTrackingUuid(UUID trackingUuid) {
        if (trackingUuid == null) {
            return null
        }
        return walletService.getByTrackingUuid(trackingUuid)
    }
}
