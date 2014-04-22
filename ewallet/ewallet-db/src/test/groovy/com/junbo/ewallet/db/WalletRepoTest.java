/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.ewallet.db;

import com.junbo.ewallet.db.entity.def.NotEnoughMoneyException;
import com.junbo.ewallet.db.repo.TransactionRepository;
import com.junbo.ewallet.db.repo.WalletRepository;
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
import java.util.Date;
import java.util.List;

/**
 * wallet repo test.
 */
public class WalletRepoTest extends BaseTest {
    @Autowired
    private WalletRepository walletRepo;
    @Autowired
    private TransactionRepository transactionRepo;

    @Test
    public void testCreate() {
        Wallet wallet = walletRepo.create(buildAWallet());
        Assert.assertNotNull(wallet.getWalletId());
        Wallet got = walletRepo.get(wallet.getWalletId());
        Assert.assertEquals(wallet.getBalance(), got.getBalance());
    }

    @Test
    public void testUpdate() {
        Wallet wallet = walletRepo.create(buildAWallet());
        wallet.setStatus(Status.LOCKED.toString());
        walletRepo.update(wallet);
        wallet = walletRepo.get(wallet.getWalletId());
        Assert.assertEquals(Status.LOCKED.toString(), wallet.getStatus());
    }

    @Test
    public void testCreditAndDebit() {
        Wallet wallet = walletRepo.create(buildAWallet());
        CreditRequest creditRequest = buildACreditRequest();
        walletRepo.credit(wallet, creditRequest);
        wallet = walletRepo.get(wallet.getWalletId());
        Assert.assertEquals(wallet.getBalance(), new BigDecimal(10));
        DebitRequest debitRequest = buildADebitRequest();
        walletRepo.debit(wallet, debitRequest);
        wallet = walletRepo.get(wallet.getWalletId());
        Assert.assertEquals(wallet.getBalance(), new BigDecimal(0));
    }

    @Test
    public void testExpiredWalletLot() {
        Wallet wallet = walletRepo.create(buildAWallet());
        CreditRequest creditRequest = buildACreditRequest();
        creditRequest.setAmount(new BigDecimal(10));
        creditRequest.setCreditType(WalletLotType.CASH.toString());
        walletRepo.credit(wallet, creditRequest);
        creditRequest.setCreditType(WalletLotType.PROMOTION.toString());
        creditRequest.setExpirationDate(new Date(new Date().getTime() - 2000));
        walletRepo.credit(wallet, creditRequest);

        DebitRequest debitRequest = buildADebitRequest();
        debitRequest.setAmount(new BigDecimal(17));
        try {
            walletRepo.debit(wallet, debitRequest);
        } catch (Exception e) {
            Assert.assertEquals(e.getClass(), NotEnoughMoneyException.class);
            Assert.assertEquals(walletRepo.get(wallet.getWalletId()).getBalance(), new BigDecimal(20));
        }
    }

    @Test
    public void testGetTransactions() {
        Wallet wallet = walletRepo.create(buildAWallet());
        CreditRequest creditRequest = buildACreditRequest();
        walletRepo.credit(wallet, creditRequest);
        DebitRequest debitRequest = buildADebitRequest();
        walletRepo.debit(wallet, debitRequest);
        List<Transaction> transactions = transactionRepo.getTransactions(wallet.getWalletId());
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
