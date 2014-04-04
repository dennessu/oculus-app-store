/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.ewallet.db.repo;

import com.junbo.ewallet.common.util.Callback;
import com.junbo.ewallet.db.dao.LotTransactionDao;
import com.junbo.ewallet.db.dao.TransactionDao;
import com.junbo.ewallet.db.dao.WalletDao;
import com.junbo.ewallet.db.dao.WalletLotDao;
import com.junbo.ewallet.db.entity.LotTransactionEntity;
import com.junbo.ewallet.db.entity.TransactionEntity;
import com.junbo.ewallet.db.entity.WalletEntity;
import com.junbo.ewallet.db.entity.WalletLotEntity;
import com.junbo.ewallet.db.entity.def.NotEnoughMoneyException;
import com.junbo.ewallet.db.entity.def.TransactionType;
import com.junbo.ewallet.db.mapper.WalletMapper;
import com.junbo.ewallet.spec.def.WalletLotType;
import com.junbo.ewallet.spec.def.WalletType;
import com.junbo.ewallet.spec.model.CreditRequest;
import com.junbo.ewallet.spec.model.DebitRequest;
import com.junbo.ewallet.spec.model.Transaction;
import com.junbo.ewallet.spec.model.Wallet;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
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
    @Autowired
    private LotTransactionDao lotTransactionDao;
    @Autowired
    private TransactionSupport transactionSupport;

    public Wallet get(Long walletId) {
        return mapper.toWallet(walletDao.get(walletId));
    }

    public Wallet get(Long userId, String type, String currency) {
        return mapper.toWallet(
                walletDao.get(userId, WalletType.valueOf(type),
                        currency == null ? null : currency));
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
        wallet.setBalance(wallet.getBalance().add(creditRequest.getAmount()));
        wallet.setTrackingUuid(creditRequest.getTrackingUuid());
        Wallet result = mapper.toWallet(walletDao.update(mapper.toWalletEntity(wallet)));

        TransactionEntity transaction =
                transactionDao.insert(buildCreditTransaction(wallet.getWalletId(), creditRequest));
        result.setTransactions(Collections.singletonList(mapper.toTransaction(transaction)));

        WalletLotEntity lotEntity = walletLotDao.insert(buildWalletLot(wallet.getWalletId(), creditRequest));
        lotTransactionDao.insert(buildCreditLotTransaction(lotEntity, transaction.getId()));

        return result;
    }

    public Wallet debit(Wallet wallet, DebitRequest debitRequest) {
        wallet.setBalance(wallet.getBalance().subtract(debitRequest.getAmount()));
        wallet.setTrackingUuid(debitRequest.getTrackingUuid());
        WalletEntity resultEntity = mapper.toWalletEntity(wallet);
        Wallet result = mapper.toWallet(walletDao.update(resultEntity));

        TransactionEntity transaction =
                transactionDao.insert(buildDebitTransaction(wallet.getWalletId(), debitRequest));
        result.setTransactions(Collections.singletonList(mapper.toTransaction(transaction)));

        List<WalletLotEntity> lots = walletLotDao.getValidLot(wallet.getWalletId());
        debit(resultEntity, lots, debitRequest.getAmount(), transaction.getId());

        return result;
    }

    private void debit(final WalletEntity wallet,
                       final List<WalletLotEntity> lots, BigDecimal sum, Long transactionId) {
        BigDecimal availableAmount = BigDecimal.ZERO;
        for (int i = 0; i < lots.size(); i++) {
            WalletLotEntity lot = lots.get(i);
            BigDecimal remaining = lot.getRemainingAmount();
            availableAmount = availableAmount.add(remaining);
            if (sum.compareTo(remaining) <= 0) {
                lot.setRemainingAmount(remaining.subtract(sum));
                walletLotDao.update(lot);
                lotTransactionDao.insert(buildDebitLotTransaction(lot, sum, transactionId));
                return;
            } else {
                BigDecimal remainingAmount = lot.getRemainingAmount();
                lot.setRemainingAmount(BigDecimal.ZERO);
                walletLotDao.update(lot);
                lotTransactionDao.insert(buildDebitLotTransaction(lot, remainingAmount, transactionId));
            }
            sum = sum.subtract(remaining);
        }

        if (sum.compareTo(BigDecimal.ZERO) > 0) {
            final BigDecimal remainingAmount = availableAmount;
            transactionSupport.executeInNewTransaction(new Callback() {
                @Override
                public void apply() {
                    wallet.setBalance(remainingAmount);
                    walletDao.update(wallet);
                }
            });
            throw new NotEnoughMoneyException();
        }
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

    private LotTransactionEntity buildDebitLotTransaction(
            WalletLotEntity lotEntity, BigDecimal amount, Long transactionId) {
        LotTransactionEntity lotTransaction = new LotTransactionEntity();
        lotTransaction.setTransactionId(transactionId);
        lotTransaction.setType(TransactionType.DEBIT);
        lotTransaction.setWalletId(lotEntity.getWalletId());
        lotTransaction.setWalletLotId(lotEntity.getId());
        lotTransaction.setAmount(amount);
        lotTransaction.setCreatedBy("DEFAULT");  //TODO
        lotTransaction.setCreatedTime(new Date());
        return lotTransaction;
    }

    private LotTransactionEntity buildCreditLotTransaction(WalletLotEntity lotEntity, Long transactionId) {
        LotTransactionEntity lotTransaction = new LotTransactionEntity();
        lotTransaction.setTransactionId(transactionId);
        lotTransaction.setType(TransactionType.CREDIT);
        lotTransaction.setWalletId(lotEntity.getWalletId());
        lotTransaction.setWalletLotId(lotEntity.getId());
        lotTransaction.setAmount(lotEntity.getRemainingAmount());
        lotTransaction.setCreatedBy("DEFAULT");  //TODO
        lotTransaction.setCreatedTime(new Date());
        return lotTransaction;
    }
}
