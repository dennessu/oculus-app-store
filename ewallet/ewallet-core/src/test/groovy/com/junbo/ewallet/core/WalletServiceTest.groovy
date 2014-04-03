package com.junbo.ewallet.core

import com.junbo.ewallet.service.WalletService
import com.junbo.ewallet.spec.model.CreditRequest
import com.junbo.ewallet.spec.model.DebitRequest
import com.junbo.ewallet.spec.model.Transaction
import com.junbo.ewallet.spec.model.Wallet
import com.junbo.sharding.IdGenerator
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests
import org.springframework.test.context.transaction.TransactionConfiguration
import org.testng.Assert
import org.testng.annotations.Test

import javax.sql.DataSource
import javax.ws.rs.WebApplicationException

/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

/**
 * Wallet Service test.
 */
@ContextConfiguration(locations = ['classpath:spring/context-test.xml'])
@TransactionConfiguration(defaultRollback = true)
@CompileStatic
class WalletServiceTest extends AbstractTransactionalTestNGSpringContextTests {
    @Override
    @Autowired
    @Qualifier('ewalletDataSource')
    public void setDataSource(DataSource dataSource) {
        super.setDataSource(dataSource)
    }

    @Autowired
    @Qualifier('oculus48IdGenerator')
    protected IdGenerator idGenerator

    @Autowired
    private WalletService walletService

    @Test
    public void testAdd() {
        Wallet wallet = buildAWallet()
        Wallet inserted = walletService.add(wallet)
        Assert.assertNotNull(inserted.getWalletId())
        Assert.assertEquals(inserted.balance, wallet.balance)
    }

    @Test
    public void testUpdate() {
        Wallet wallet = walletService.add(buildAWallet())
        wallet.status = com.junbo.ewallet.spec.def.Status.LOCKED.toString()
        Wallet updated = walletService.update(wallet.walletId, wallet)
        Assert.assertEquals(updated.status, com.junbo.ewallet.spec.def.Status.LOCKED.toString())
    }

    @Test
    public void testCreditAndDebit() {
        Wallet wallet = walletService.add(buildAWallet())
        CreditRequest creditRequest = buildACreditRequest()
        creditRequest.setWalletId(wallet.walletId)
        walletService.credit(creditRequest)
        creditRequest.type = com.junbo.ewallet.spec.def.WalletLotType.CASH.toString()
        wallet = walletService.credit(creditRequest)
        Assert.assertEquals(wallet.balance, new BigDecimal(20))
        DebitRequest debitRequest = buildADebitRequest()
        wallet = walletService.debit(wallet.walletId, debitRequest)
        Assert.assertEquals(wallet.balance, new BigDecimal(3))
    }

    @Test(expectedExceptions = [WebApplicationException])
    public void testExpiredCredit() {
        Wallet wallet = walletService.add(buildAWallet())
        CreditRequest creditRequest = buildACreditRequest()
        creditRequest.setWalletId(wallet.walletId)
        creditRequest.expirationDate = new Date(100000)
        creditRequest.amount = new BigDecimal(100)
        walletService.credit(creditRequest)
        walletService.debit(wallet.walletId, buildADebitRequest())
    }

    @Test(expectedExceptions = [WebApplicationException])
    public void testNotEnoughMoney() {
        Wallet wallet = walletService.add(buildAWallet())
        CreditRequest creditRequest = buildACreditRequest()
        walletService.credit(creditRequest)
        DebitRequest debitRequest = buildADebitRequest()
        walletService.debit(wallet.walletId, debitRequest)
    }

    @Test
    public void testGetTransactions() {
        Wallet wallet = walletService.add(buildAWallet())
        CreditRequest creditRequest = buildACreditRequest()
        creditRequest.setWalletId(wallet.walletId)
        walletService.credit(creditRequest)
        creditRequest.type = com.junbo.ewallet.spec.def.WalletLotType.CASH.toString()
        walletService.credit(creditRequest)
        DebitRequest debitRequest = buildADebitRequest()
        walletService.debit(wallet.walletId, debitRequest)
        List<Transaction> transactionList = walletService.getTransactions(wallet.walletId).transactions
        Assert.assertEquals(transactionList.size(), 3)
    }

    private Wallet buildAWallet() {
        Wallet wallet = new Wallet()
        wallet.setUserId(idGenerator.nextId())
        wallet.setType(com.junbo.ewallet.spec.def.WalletType.SV.toString())
        wallet.setCurrency(com.junbo.ewallet.spec.def.Currency.USD.toString())
        wallet.setBalance(BigDecimal.ZERO)
        return wallet
    }

    private CreditRequest buildACreditRequest(){
        CreditRequest creditRequest = new CreditRequest()
        creditRequest.setType(com.junbo.ewallet.spec.def.WalletLotType.PROMOTION.toString())
        creditRequest.setOfferId(idGenerator.nextId())
        creditRequest.setAmount(new BigDecimal(10))
        return creditRequest
    }

    private DebitRequest buildADebitRequest() {
        DebitRequest request = new DebitRequest()
        request.setAmount(new BigDecimal(17))
        request.setOfferId(idGenerator.nextId())
        return request
    }
}
