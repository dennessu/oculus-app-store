/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.ewallet.db.mapper;

import com.junbo.ewallet.db.entity.TransactionEntity;
import com.junbo.ewallet.db.entity.WalletEntity;
import com.junbo.ewallet.spec.def.Status;
import com.junbo.ewallet.spec.def.WalletType;
import com.junbo.ewallet.spec.model.Transaction;
import com.junbo.ewallet.spec.model.Wallet;

import java.util.ArrayList;
import java.util.List;

/**
 * mapper for wallet and transaction.
 */
public class WalletMapper {
    public Wallet toWallet(WalletEntity walletEntity) {
        if (walletEntity == null) {
            return null;
        }
        Wallet wallet = new Wallet();
        wallet.setWalletId(walletEntity.getpId());
        wallet.setBalance(walletEntity.getBalance());
        wallet.setCurrency(walletEntity.getCurrency());
        wallet.setStatus(walletEntity.getStatus().toString());
        wallet.setUserId(walletEntity.getUserId());
        wallet.setType(walletEntity.getType().toString());
        wallet.setTrackingUuid(walletEntity.getTrackingUuid());
        return wallet;
    }

    public WalletEntity toWalletEntity(Wallet wallet) {
        WalletEntity walletEntity = new WalletEntity();
        walletEntity.setUserId(wallet.getUserId());
        walletEntity.setpId(wallet.getWalletId());
        walletEntity.setStatus(Status.valueOf(wallet.getStatus().toUpperCase()));
        walletEntity.setType(WalletType.valueOf(wallet.getType().toUpperCase()));
        walletEntity.setCurrency(wallet.getCurrency().toUpperCase());
        walletEntity.setBalance(wallet.getBalance());
        walletEntity.setTrackingUuid(wallet.getTrackingUuid());
        return walletEntity;
    }

    public List<Wallet> toWallets(List<WalletEntity> walletEntities) {
        List<Wallet> wallets = new ArrayList<>(walletEntities.size());
        for (WalletEntity entity : walletEntities) {
            wallets.add(toWallet(entity));
        }
        return wallets;
    }

    public List<Transaction> toTransactions(List<TransactionEntity> transactionEntities) {
        List<Transaction> transactions = new ArrayList<Transaction>(transactionEntities.size());
        for (TransactionEntity entity : transactionEntities) {
            transactions.add(toTransaction(entity));
        }
        return transactions;
    }

    public Transaction toTransaction(TransactionEntity transactionEntity) {
        if (transactionEntity == null) {
            return null;
        }
        Transaction transaction = new Transaction();
        transaction.setTransactionId(transactionEntity.getpId());
        transaction.setWalletId(transactionEntity.getWalletId());
        transaction.setType(transactionEntity.getType().toString());
        transaction.setAmount(transactionEntity.getAmount());
        transaction.setType(transactionEntity.getType().toString());
        transaction.setOfferId(transactionEntity.getOfferId());
        transaction.setCreatedBy(transactionEntity.getCreatedBy().toString());
        transaction.setCreatedTime(transactionEntity.getCreatedTime());
        transaction.setUnrefundedAmount(transactionEntity.getUnrefundedAmount());
        return transaction;
    }
}
