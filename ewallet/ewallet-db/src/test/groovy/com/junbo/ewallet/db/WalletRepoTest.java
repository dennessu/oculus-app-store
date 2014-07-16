/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.ewallet.db;

import com.junbo.ewallet.db.repo.facade.TransactionRepositoryFacade;
import com.junbo.ewallet.db.repo.facade.WalletRepositoryFacade;
import com.junbo.ewallet.spec.def.Currency;
import com.junbo.ewallet.spec.def.Status;
import com.junbo.ewallet.spec.def.WalletLotType;
import com.junbo.ewallet.spec.def.WalletType;
import com.junbo.ewallet.spec.model.CreditRequest;
import com.junbo.ewallet.spec.model.DebitRequest;
import com.junbo.ewallet.spec.model.Transaction;
import com.junbo.ewallet.spec.model.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.List;

/**
 * wallet repo test.
 */
public class WalletRepoTest extends BaseTest {
    @Autowired
    private WalletRepositoryFacade walletRepo;
    @Autowired
    private TransactionRepositoryFacade transactionRepo;

    @Test
    public void testCreate() {
        Wallet wallet = walletRepo.create(buildAWallet());
        Assert.assertNotNull(wallet.getId());
        Wallet got = walletRepo.get(wallet.getId());
        Assert.assertEquals(wallet.getBalance(), got.getBalance());
    }

    @Test
    public void testUpdate() {
        Wallet wallet = walletRepo.create(buildAWallet());
        wallet.setStatus(Status.LOCKED.toString());
        walletRepo.update(wallet, wallet);
        wallet = walletRepo.get(wallet.getId());
        Assert.assertEquals(Status.LOCKED.toString(), wallet.getStatus());
    }

    @Test
    public void testCreditAndDebit() {
        Wallet wallet = walletRepo.create(buildAWallet());
        CreditRequest creditRequest = buildACreditRequest();
        walletRepo.credit(wallet, creditRequest);
        wallet = walletRepo.get(wallet.getId());
        Assert.assertEquals(wallet.getBalance(), new BigDecimal(10));
        DebitRequest debitRequest = buildADebitRequest();
        walletRepo.debit(wallet, debitRequest);
        wallet = walletRepo.get(wallet.getId());
        Assert.assertEquals(wallet.getBalance(), new BigDecimal(0));
    }

    @Test
    public void testGetTransactions() {
        Wallet wallet = walletRepo.create(buildAWallet());
        CreditRequest creditRequest = buildACreditRequest();
        walletRepo.credit(wallet, creditRequest);
        wallet = walletRepo.get(wallet.getId());
        DebitRequest debitRequest = buildADebitRequest();
        walletRepo.debit(wallet, debitRequest);
        List<Transaction> transactions = transactionRepo.getTransactions(wallet.getId());
        Assert.assertEquals(transactions.size(), 2);
    }

    private Wallet buildAWallet() {
        Wallet wallet = new Wallet();
        wallet.setUserId(idGenerator.nextId());
        wallet.setType(WalletType.STORED_VALUE.toString());
        wallet.setStatus(Status.ACTIVE.toString());
        wallet.setCurrency(Currency.USD.toString());
        wallet.setBalance(BigDecimal.ZERO);
        return wallet;
    }

    private CreditRequest buildACreditRequest() {
        CreditRequest creditRequest = new CreditRequest();
        creditRequest.setCreditType(WalletLotType.PROMOTION.toString());
        creditRequest.setOfferId(idGenerator.nextId());
        creditRequest.setAmount(new BigDecimal(10));
        return creditRequest;
    }

    private DebitRequest buildADebitRequest() {
        DebitRequest request = new DebitRequest();
        request.setAmount(new BigDecimal(10));
        request.setOfferId(idGenerator.nextId());
        return request;
    }
}
