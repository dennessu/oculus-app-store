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
import com.junbo.ewallet.spec.model.Transaction
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
    private  WalletService walletService
    @Override
    Promise<Wallet> getWallet(WalletId walletId) {
        Wallet result = walletService.get(walletId.value)
        return Promise.pure(result)
    }

    @Override
    Promise<Wallet> postWallet(Wallet wallet) {
        Wallet result = walletService.add(wallet)
        return Promise.pure(result)
    }

    @Override
    Promise<Wallet> updateWallet(WalletId walletId, Wallet wallet) {
        Wallet result = walletService.update(walletId.value, wallet)
        return Promise.pure(result)
    }

    @Override
    Promise<Wallet> credit(WalletId walletId, CreditRequest creditRequest) {
        Wallet result = walletService.credit(walletId.value, creditRequest)
        return Promise.pure(result)
    }

    @Override
    Promise<Wallet> debit(WalletId walletId, DebitRequest debitRequest) {
        Wallet result = walletService.debit(walletId.value, debitRequest)
        return Promise.pure(result)
    }

    @Override
    Promise<Wallet> getTransactions(WalletId walletId) {
        Wallet result = walletService.get(walletId.value)
        List<Transaction> transactions = walletService.getTransactions(walletId.value)
        result.transactions = transactions
        return Promise.pure(result)
    }
}
