/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.ewallet.db.mapper;

import com.junbo.ewallet.db.entity.TransactionEntity;
import com.junbo.ewallet.db.entity.WalletEntity;
import com.junbo.ewallet.db.entity.def.Status;
import com.junbo.ewallet.db.entity.def.WalletType;
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
        wallet.setWalletId(walletEntity.getId());
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
        walletEntity.setId(wallet.getWalletId());
        walletEntity.setStatus(Status.valueOf(wallet.getStatus()));
        walletEntity.setType(WalletType.valueOf(wallet.getType()));
        walletEntity.setCurrency(wallet.getCurrency());
        walletEntity.setBalance(wallet.getBalance());
        walletEntity.setTrackingUuid(wallet.getTrackingUuid());
        return walletEntity;
    }


    public List<Transaction> toTransactions(List<TransactionEntity> transactionEntities) {
        List<Transaction> transactions = new ArrayList<Transaction>(transactionEntities.size());
        for (TransactionEntity entity : transactionEntities) {
            transactions.add(toTransaction(entity));
        }
        return transactions;
    }

    private Transaction toTransaction(TransactionEntity transactionEntity) {
        Transaction transaction = new Transaction();
        transaction.setType(transactionEntity.getType().toString());
        transaction.setAmount(transactionEntity.getAmount());
        transaction.setType(transactionEntity.getType().toString());
        transaction.setOfferId(transactionEntity.getOfferId());
        transaction.setCreatedBy(transactionEntity.getCreatedBy());
        transaction.setCreatedTime(transactionEntity.getCreatedTime());
        return transaction;
    }
}
