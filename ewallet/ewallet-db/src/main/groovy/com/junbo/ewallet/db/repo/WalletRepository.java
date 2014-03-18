/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.ewallet.db.repo;

import com.junbo.ewallet.db.dao.TransactionDao;
import com.junbo.ewallet.db.dao.WalletDao;
import com.junbo.ewallet.db.dao.WalletLotDao;
import com.junbo.ewallet.db.entity.def.Currency;
import com.junbo.ewallet.db.entity.def.TransactionType;
import com.junbo.ewallet.db.entity.def.WalletLotType;
import com.junbo.ewallet.db.entity.def.WalletType;
import com.junbo.ewallet.db.entity.hibernate.TransactionEntity;
import com.junbo.ewallet.db.entity.hibernate.WalletEntity;
import com.junbo.ewallet.db.entity.hibernate.WalletLotEntity;
import com.junbo.ewallet.db.mapper.WalletMapper;
import com.junbo.ewallet.spec.model.CreditRequest;
import com.junbo.ewallet.spec.model.DebitRequest;
import com.junbo.ewallet.spec.model.Transaction;
import com.junbo.ewallet.spec.model.Wallet;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

/**
 * repo of wallet.
 */
public class WalletRepository {
    @Autowired
    private WalletMapper mapper;
    @Autowired
    private WalletDao walletDao;
    @Autowired
    private WalletLotDao walletLotDao;
    @Autowired
    private TransactionDao transactionDao;

    public Wallet get(Long walletId) {
        return mapper.toWallet(walletDao.get(walletId));
    }

    public Wallet get(Long userId, String type, String currency) {
        return mapper.toWallet(
                walletDao.get(userId, WalletType.valueOf(type),
                        currency == null ? null : Currency.valueOf(currency)));
    }

    public Wallet create(Wallet wallet) {
        WalletEntity result = walletDao.insert(mapper.toWalletEntity(wallet));
        return mapper.toWallet(result);
    }

    public Wallet update(Wallet wallet) {
        WalletEntity result = walletDao.update(mapper.toWalletEntity(wallet));
        return mapper.toWallet(result);
    }

    public Wallet credit(Wallet wallet, CreditRequest creditRequest) {
        walletLotDao.insert(buildWalletLot(wallet.getWalletId(), creditRequest));
        transactionDao.insert(buildCreditTransaction(wallet.getWalletId(), creditRequest));
        wallet.setBalance(wallet.getBalance().add(creditRequest.getAmount()));
        wallet.setTrackingUuid(creditRequest.getTrackingUuid());
        return update(wallet);
    }

    public Wallet debit(Wallet wallet, DebitRequest debitRequest) {
        walletLotDao.debit(wallet.getWalletId(), debitRequest.getAmount());
        transactionDao.insert(buildDebitTransaction(wallet.getWalletId(), debitRequest));
        wallet.setBalance(wallet.getBalance().subtract(debitRequest.getAmount()));
        wallet.setTrackingUuid(debitRequest.getTrackingUuid());
        return update(wallet);
    }

    public List<Transaction> getTransactions(Long walletId) {
        List<TransactionEntity> results = transactionDao.getByWalletId(walletId);
        return mapper.toTransactions(results);
    }

    public Wallet getByTrackingUuid(UUID uuid) {
        return mapper.toWallet(walletDao.getByTrackingUuid(uuid));
    }

    private WalletLotEntity buildWalletLot(Long walletId, CreditRequest creditRequest) {
        WalletLotEntity lotEntity = new WalletLotEntity();
        lotEntity.setWalletId(walletId);
        lotEntity.setType(WalletLotType.valueOf(creditRequest.getType()));
        lotEntity.setTotalAmount(creditRequest.getAmount());
        lotEntity.setRemainingAmount(creditRequest.getAmount());
        lotEntity.setExpirationDate(creditRequest.getExpirationDate());
        return lotEntity;
    }

    private TransactionEntity buildCreditTransaction(Long walletId, CreditRequest creditRequest) {
        TransactionEntity transactionEntity = new TransactionEntity();
        transactionEntity.setWalletId(walletId);
        transactionEntity.setAmount(creditRequest.getAmount());
        transactionEntity.setOfferId(creditRequest.getOfferId());
        transactionEntity.setType(TransactionType.CREDIT);
        return transactionEntity;
    }

    private TransactionEntity buildDebitTransaction(Long walletId, DebitRequest debitRequest) {
        TransactionEntity transactionEntity = new TransactionEntity();
        transactionEntity.setWalletId(walletId);
        transactionEntity.setAmount(debitRequest.getAmount());
        transactionEntity.setOfferId(debitRequest.getOfferId());
        transactionEntity.setType(TransactionType.DEBIT);
        return transactionEntity;
    }


}
