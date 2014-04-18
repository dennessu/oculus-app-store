/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.ewallet.rest

import com.junbo.common.id.WalletId
import com.junbo.common.model.Results
import com.junbo.ewallet.spec.model.CreditRequest
import com.junbo.ewallet.spec.model.DebitRequest
import com.junbo.ewallet.spec.model.Transaction
import com.junbo.ewallet.spec.model.Wallet
import com.junbo.ewallet.spec.resource.proxy.WalletResourceClientProxy
import com.junbo.sharding.IdGenerator
import junit.framework.Assert
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.testng.annotations.Test

/**
 * test for wallet clientProxy.
 */
@ContextConfiguration(locations = ['classpath:spring/context-test.xml'])
public class WalletClientProxyTest extends AbstractTestNGSpringContextTests {
    @Autowired
    private WalletResourceClientProxy clientProxy
    @Autowired
    @Qualifier('oculus48IdGenerator')
    private IdGenerator idGenerator;

    @Test(enabled = false)
    public void testCreate() {
        Wallet wallet = buildAWallet()
        Wallet result = clientProxy.postWallet(wallet).wrapped().get()
        Assert.assertNotNull(result.walletId)
    }

    @Test(enabled = false)
    public void testUpdate() {
        Wallet wallet = buildAWallet()
        Wallet result = clientProxy.postWallet(wallet).wrapped().get()
        result.setStatus(com.junbo.ewallet.spec.def.Status.LOCKED.toString())
        result = clientProxy.updateWallet(new WalletId(result.walletId), result).wrapped().get()
        Assert.assertEquals(result.status, com.junbo.ewallet.spec.def.Status.LOCKED.toString())
    }

    @Test(enabled = false)
    public void testCreditAndDebitAndGetTransactions() {
        Wallet wallet = buildAWallet()
        wallet = clientProxy.postWallet(wallet).wrapped().get()
        def creditRequest = buildACreditRequest()
        creditRequest.setUserId(wallet.userId)
        clientProxy.credit(creditRequest).wrapped().get()
        wallet = clientProxy.getWallet(new WalletId(wallet.walletId)).wrapped().get()
        Assert.assertEquals(wallet.balance, new BigDecimal(10))
        clientProxy.credit(buildACreditRequest(wallet.walletId)).wrapped().get()
        wallet = clientProxy.getWallet(new WalletId(wallet.walletId)).wrapped().get()
        Assert.assertEquals(wallet.balance, new BigDecimal(20))
        clientProxy.debit(new WalletId(wallet.walletId), buildADebitRequest()).wrapped().get()
        wallet = clientProxy.getWallet(new WalletId(wallet.walletId)).wrapped().get()
        Assert.assertEquals(wallet.balance, BigDecimal.ZERO)
        Results<Transaction> transactionResults = clientProxy.getTransactions(new WalletId(wallet.walletId)).wrapped().get()
        Assert.assertEquals(transactionResults.items.size(), 3)
    }

    private Wallet buildAWallet() {
        Wallet wallet = new Wallet()
        wallet.setType(com.junbo.ewallet.spec.def.WalletType.STORED_VALUE.toString())
        wallet.setCurrency(com.junbo.ewallet.spec.def.Currency.USD.toString())
        wallet.setUserId(idGenerator.nextId())
        return wallet
    }

    private CreditRequest buildACreditRequest() {
        CreditRequest creditRequest = new CreditRequest()
        creditRequest.setCreditType(com.junbo.ewallet.spec.def.WalletLotType.CASH.toString())
        creditRequest.setWalletType(com.junbo.ewallet.spec.def.WalletType.STORED_VALUE.toString())
        creditRequest.setAmount(new BigDecimal(10))
        creditRequest.setCurrency(com.junbo.ewallet.spec.def.Currency.USD.toString())
        return creditRequest
    }

    private CreditRequest buildACreditRequest(Long walletId) {
        CreditRequest creditRequest = new CreditRequest()
        creditRequest.setCreditType(com.junbo.ewallet.spec.def.WalletLotType.CASH.toString())
        creditRequest.setAmount(new BigDecimal(10))
        creditRequest.setWalletId(walletId)
        return creditRequest
    }

    private DebitRequest buildADebitRequest() {
        DebitRequest request = new DebitRequest()
        request.setAmount(new BigDecimal(20))
        return request
    }
}
