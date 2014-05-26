/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.ewallet.db.repo;

import com.junbo.ewallet.common.def.WalletConst;
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
import com.junbo.ewallet.spec.model.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.*;

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

    public Transaction credit(Wallet wallet, CreditRequest creditRequest) {
        wallet.setBalance(wallet.getBalance().add(creditRequest.getAmount()));
        walletDao.update(mapper.toWalletEntity(wallet));

        TransactionEntity transaction =
                transactionDao.insert(buildCreditTransaction(
                        creditRequest.getTrackingUuid(), wallet.getWalletId(), creditRequest));

        WalletLotEntity lotEntity = walletLotDao.insert(buildWalletLot(wallet.getWalletId(), creditRequest));
        lotTransactionDao.insert(buildCreditLotTransaction(lotEntity, transaction.getId()));

        return mapper.toTransaction(transaction);
    }

    public Transaction debit(Wallet wallet, DebitRequest debitRequest) {
        wallet.setBalance(wallet.getBalance().subtract(debitRequest.getAmount()));
        WalletEntity resultEntity = mapper.toWalletEntity(wallet);
        walletDao.update(resultEntity);

        TransactionEntity transaction =
                transactionDao.insert(buildDebitTransaction(
                        debitRequest.getTrackingUuid(), wallet.getWalletId(), debitRequest));

        List<WalletLotEntity> lots = walletLotDao.getValidLot(wallet.getWalletId());
        debit(resultEntity, lots, debitRequest.getAmount(), transaction.getId());

        return mapper.toTransaction(transaction);
    }

    private void debit(final WalletEntity wallet,
                       final List<WalletLotEntity> lots, BigDecimal sum, Long transactionId) {
        Collections.sort(lots, new Comparator<WalletLotEntity>() {
            @Override
            public int compare(WalletLotEntity o1, WalletLotEntity o2) {
                return o1.getRemainingAmount().compareTo(o2.getRemainingAmount());
            }
        });
        for (int i = 0; i < lots.size(); i++) {
            WalletLotEntity lot = lots.get(i);
            BigDecimal remaining = lot.getRemainingAmount();
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
            transactionSupport.executeInNewTransaction(new Callback() {
                @Override
                public void apply() {
                    wallet.setBalance(walletLotDao.getValidAmount(wallet.getId()));
                    walletDao.update(wallet);
                }
            });
            throw new NotEnoughMoneyException();
        }
    }

    public Transaction refund(Wallet wallet, Long transactionId, RefundRequest refundRequest) {
        wallet.setBalance(wallet.getBalance().add(refundRequest.getAmount()));
        walletDao.update(mapper.toWalletEntity(wallet));
        TransactionEntity debitTransaction = transactionDao.get(transactionId);
        debitTransaction.setUnrefundedAmount(debitTransaction.getUnrefundedAmount().subtract(refundRequest.getAmount()));
        transactionDao.update(debitTransaction);

        TransactionEntity transaction =
                transactionDao.insert(buildRefundTransaction(
                        refundRequest.getTrackingUuid(), wallet.getWalletId(), refundRequest));

        List<LotTransactionEntity> lotTransactions = lotTransactionDao.getByTransactionId(transactionId);
        refund(lotTransactions, refundRequest.getAmount(), transaction.getId());
        return mapper.toTransaction(transaction);
    }

    private void refund(List<LotTransactionEntity> lotTransactions, BigDecimal amount, Long transactionId) {
        Date now = new Date();
        Boolean refundEnded = false;
        for (LotTransactionEntity lotTransaction : lotTransactions) {
            WalletLotEntity lot = walletLotDao.get(lotTransaction.getWalletLotId());
            if (amount.subtract(lotTransaction.getUnrefundedAmount()).compareTo(BigDecimal.ZERO) <= 0) {
                lot.setRemainingAmount(lot.getRemainingAmount().add(amount));
                lotTransaction.setUnrefundedAmount(lotTransaction.getUnrefundedAmount().subtract(amount));
                lotTransactionDao.insert(buildRefundLotTransaction(lot, amount, transactionId));
                refundEnded = true;
            } else {
                lot.setRemainingAmount(lot.getRemainingAmount().add(lotTransaction.getUnrefundedAmount()));
                lotTransactionDao.insert(buildRefundLotTransaction(lot, lotTransaction.getUnrefundedAmount(), transactionId));
                amount = amount.subtract(lotTransaction.getUnrefundedAmount());
                lotTransaction.setUnrefundedAmount(BigDecimal.ZERO);
            }

            if (lot.getExpirationDate() != null && lot.getExpirationDate().before(now)) {
                lot.setExpirationDate(WalletConst.NEVER_EXPIRE);  //enable lot
            }
            walletLotDao.update(lot);
            lotTransactionDao.update(lotTransaction);
            if (refundEnded) {
                break;
            }
        }
    }

    public Wallet getByTrackingUuid(Long shardMasterId, UUID uuid) {
        return mapper.toWallet(walletDao.getByTrackingUuid(shardMasterId, uuid));
    }

    private WalletLotEntity buildWalletLot(Long walletId, CreditRequest creditRequest) {
        WalletLotEntity lotEntity = new WalletLotEntity();
        lotEntity.setWalletId(walletId);
        lotEntity.setType(WalletLotType.valueOf(creditRequest.getCreditType()));
        lotEntity.setTotalAmount(creditRequest.getAmount());
        lotEntity.setRemainingAmount(creditRequest.getAmount());
        lotEntity.setExpirationDate(
                creditRequest.getExpirationDate() == null ? WalletConst.NEVER_EXPIRE : creditRequest.getExpirationDate());
        return lotEntity;
    }

    private TransactionEntity buildCreditTransaction(UUID trackingUuid, Long walletId, CreditRequest creditRequest) {
        TransactionEntity transactionEntity = new TransactionEntity();
        transactionEntity.setTrackingUuid(trackingUuid);
        transactionEntity.setWalletId(walletId);
        transactionEntity.setAmount(creditRequest.getAmount());
        transactionEntity.setOfferId(creditRequest.getOfferId());
        transactionEntity.setType(TransactionType.CREDIT);
        return transactionEntity;
    }

    private TransactionEntity buildDebitTransaction(UUID trackingUuid, Long walletId, DebitRequest debitRequest) {
        TransactionEntity transactionEntity = new TransactionEntity();
        transactionEntity.setTrackingUuid(trackingUuid);
        transactionEntity.setWalletId(walletId);
        transactionEntity.setAmount(debitRequest.getAmount());
        transactionEntity.setOfferId(debitRequest.getOfferId());
        transactionEntity.setType(TransactionType.DEBIT);
        transactionEntity.setUnrefundedAmount(debitRequest.getAmount());
        return transactionEntity;
    }

    private TransactionEntity buildRefundTransaction(UUID trackingUuid, Long walletId, RefundRequest refundRequest) {
        TransactionEntity transactionEntity = new TransactionEntity();
        transactionEntity.setTrackingUuid(trackingUuid);
        transactionEntity.setWalletId(walletId);
        transactionEntity.setAmount(refundRequest.getAmount());
        transactionEntity.setType(TransactionType.REFUND);
        return transactionEntity;
    }

    private LotTransactionEntity buildDebitLotTransaction(
            WalletLotEntity lotEntity, BigDecimal amount, Long transactionId) {
        LotTransactionEntity lotTransaction = new LotTransactionEntity();
        lotTransaction.setTransactionId(transactionId);
        lotTransaction.setType(TransactionType.DEBIT);
        lotTransaction.setWalletLotType(lotEntity.getType());
        lotTransaction.setWalletId(lotEntity.getWalletId());
        lotTransaction.setWalletLotId(lotEntity.getId());
        lotTransaction.setAmount(amount);
        lotTransaction.setUnrefundedAmount(amount);
        return lotTransaction;
    }

    private LotTransactionEntity buildCreditLotTransaction(WalletLotEntity lotEntity, Long transactionId) {
        LotTransactionEntity lotTransaction = new LotTransactionEntity();
        lotTransaction.setTransactionId(transactionId);
        lotTransaction.setType(TransactionType.CREDIT);
        lotTransaction.setWalletLotType(lotEntity.getType());
        lotTransaction.setWalletId(lotEntity.getWalletId());
        lotTransaction.setWalletLotId(lotEntity.getId());
        lotTransaction.setAmount(lotEntity.getRemainingAmount());
        return lotTransaction;
    }

    private LotTransactionEntity buildRefundLotTransaction(WalletLotEntity lotEntity, BigDecimal amount, Long transactionId) {
        LotTransactionEntity lotTransaction = new LotTransactionEntity();
        lotTransaction.setTransactionId(transactionId);
        lotTransaction.setType(TransactionType.REFUND);
        lotTransaction.setWalletLotType(lotEntity.getType());
        lotTransaction.setWalletId(lotEntity.getWalletId());
        lotTransaction.setWalletLotId(lotEntity.getId());
        lotTransaction.setAmount(amount);
        return lotTransaction;
    }

    public List<Wallet> getAll(long userId) {
        return mapper.toWallets(walletDao.getAll(userId));
    }
}
