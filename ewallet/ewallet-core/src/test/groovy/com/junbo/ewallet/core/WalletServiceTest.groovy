package com.junbo.ewallet.core

import com.junbo.ewallet.db.repo.WalletRepository
import com.junbo.ewallet.service.WalletService
import com.junbo.ewallet.service.impl.TransactionSupport
import com.junbo.ewallet.spec.model.*
import com.junbo.sharding.IdGenerator
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.springframework.test.context.transaction.TransactionConfiguration
import org.springframework.test.context.transaction.TransactionalTestExecutionListener
import org.springframework.transaction.annotation.Transactional
import org.testng.Assert
import org.testng.annotations.Test

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
class WalletServiceTest extends AbstractTestNGSpringContextTests {
    @Autowired
    @Qualifier('oculus48IdGenerator')
    protected IdGenerator idGenerator

    @Autowired
    private WalletService walletService
    @Autowired
    private WalletRepository walletRepo

    @Test
    public void testAdd() {
        Wallet wallet = buildAWallet()
        Wallet inserted = walletService.add(wallet)
        Assert.assertNotNull(inserted.getId())
        Assert.assertEquals(inserted.balance, wallet.balance)
    }

    @Test
    public void testUpdate() {
        Wallet wallet = walletService.add(buildAWallet())
        wallet.status = com.junbo.ewallet.spec.def.Status.LOCKED.toString()
        Wallet updated = walletService.update(wallet.getId(), wallet)
        Assert.assertEquals(updated.status, com.junbo.ewallet.spec.def.Status.LOCKED.toString())
    }

    @Test
    public void testCreditAndDebit() {
        Wallet wallet = walletService.add(buildAWallet())
        CreditRequest creditRequest = buildACreditRequest()
        creditRequest.setWalletId(wallet.getId())
        walletService.credit(creditRequest)
        creditRequest.creditType = com.junbo.ewallet.spec.def.WalletLotType.CASH.toString()
        walletService.credit(creditRequest)
        wallet = walletService.get(wallet.getId())
        Assert.assertEquals(wallet.balance, new BigDecimal(20))
        DebitRequest debitRequest = buildADebitRequest()
        walletService.debit(wallet.getId(), debitRequest)
        wallet = walletService.get(wallet.getId())
        Assert.assertEquals(wallet.balance, new BigDecimal(3))
    }

    @Test
    public void testRefund() {
        Wallet wallet = walletService.add(buildAWallet())
        CreditRequest creditRequest = buildACreditRequest()
        creditRequest.setWalletId(wallet.getId())
        walletService.credit(creditRequest)

        DebitRequest debitRequest = buildADebitRequest()
        debitRequest.amount = new BigDecimal(5)
        walletService.debit(wallet.getId(), debitRequest)

        Transaction transaction = walletService.getTransactions(wallet.getId()).get(1)
        wallet = walletService.get(wallet.getId())
        Assert.assertEquals(wallet.balance, new BigDecimal(5))

        RefundRequest refundRequest = buildARefundRequest(transaction.getId())
        walletService.refund(transaction.getId(), refundRequest)
        wallet = walletService.get(wallet.getId())
        Assert.assertEquals(wallet.balance, new BigDecimal(7))

        walletService.refund(transaction.getId(), refundRequest)
        wallet = walletService.get(wallet.getId())
        Assert.assertEquals(wallet.balance, new BigDecimal(9))
    }

    @Test(expectedExceptions = [WebApplicationException])
    public void testExpiredCredit() {
        Wallet wallet = walletService.add(buildAWallet())
        CreditRequest creditRequest = buildACreditRequest()
        creditRequest.setWalletId(wallet.getId())
        creditRequest.expirationDate = new Date(100000)
        creditRequest.amount = new BigDecimal(100)
        walletService.credit(creditRequest)
    }

    @Test(expectedExceptions = [WebApplicationException])
    public void testNotEnoughMoney() {
        Wallet wallet = walletService.add(buildAWallet())
        CreditRequest creditRequest = buildACreditRequest()
        walletService.credit(creditRequest)
        DebitRequest debitRequest = buildADebitRequest()
        walletService.debit(wallet.getId(), debitRequest)
    }

    @Test
    public void testGetTransactions() {
        Wallet wallet = walletService.add(buildAWallet())
        CreditRequest creditRequest = buildACreditRequest()
        creditRequest.setWalletId(wallet.getId())
        walletService.credit(creditRequest)
        creditRequest.creditType = com.junbo.ewallet.spec.def.WalletLotType.CASH.toString()
        walletService.credit(creditRequest)
        DebitRequest debitRequest = buildADebitRequest()
        walletService.debit(wallet.getId(), debitRequest)
        List<Transaction> transactionList = walletService.getTransactions(wallet.getId())
        Assert.assertEquals(transactionList.size(), 3)
    }

    private Wallet buildAWallet() {
        Wallet wallet = new Wallet()
        wallet.setUserId(idGenerator.nextId())
        wallet.setType(com.junbo.ewallet.spec.def.WalletType.STORED_VALUE.toString())
        wallet.setCurrency(com.junbo.ewallet.spec.def.Currency.USD.toString())
        wallet.setBalance(BigDecimal.ZERO)
        return wallet
    }

    private CreditRequest buildACreditRequest() {
        CreditRequest creditRequest = new CreditRequest()
        creditRequest.setCreditType(com.junbo.ewallet.spec.def.WalletLotType.CASH.toString())
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

    private RefundRequest buildARefundRequest(Long transactionId) {
        RefundRequest request = new RefundRequest()
        request.setAmount(new BigDecimal(2))
        request.setCurrency("USD")
        return request
    }
}
