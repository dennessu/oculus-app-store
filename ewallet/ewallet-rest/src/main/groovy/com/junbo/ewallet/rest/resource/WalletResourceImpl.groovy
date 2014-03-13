/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.ewallet.rest.resource
import com.junbo.common.id.UserId
import com.junbo.common.id.WalletId
import com.junbo.ewallet.spec.model.CreditRequest
import com.junbo.ewallet.spec.model.DebitRequest
import com.junbo.ewallet.spec.model.ResultList
import com.junbo.ewallet.spec.model.Wallet
import com.junbo.ewallet.spec.resource.WalletResource
import com.junbo.langur.core.promise.Promise
/**
 * WalletResource Impl.
 */
class WalletResourceImpl implements WalletResource {
    @Override
    Promise<Wallet> getWallet(WalletId walletId) {
        return null
    }

    @Override
    Promise<ResultList<Wallet>> getWallets(UserId userId) {
        return null
    }

    @Override
    Promise<Wallet> updateWallet(WalletId walletId, Wallet wallet) {
        return null
    }

    @Override
    Promise<Wallet> credit(WalletId walletId, CreditRequest creditRequest) {
        return null
    }

    @Override
    Promise<Wallet> debit(WalletId walletId, DebitRequest debitRequest) {
        return null
    }

    @Override
    Promise<Wallet> getTransactions(WalletId walletId) {
        return null
    }
}
