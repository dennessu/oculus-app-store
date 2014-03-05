/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.ewallet.rest.resource
import com.junbo.ewallet.spec.model.ResultList
import com.junbo.ewallet.spec.model.Wallet
import com.junbo.ewallet.spec.resource.WalletResource
import com.junbo.langur.core.promise.Promise
/**
 * WalletResource Impl.
 */
class WalletResourceImpl implements WalletResource {
    @Override
    Promise<Wallet> getWallet(Long walletId) {
        return null
    }

    @Override
    Promise<ResultList<Wallet>> getWallets(Long userId) {
        return null
    }

    @Override
    Promise<Wallet> updateWallet(Long walletId, Wallet wallet) {
        return null
    }

    @Override
    Promise<Wallet> credit(Wallet wallet) {
        return null
    }

    @Override
    Promise<Wallet> debit(Wallet wallet) {
        return null
    }

    @Override
    Promise<Wallet> getTransactions(Long walletId) {
        return null
    }
}
