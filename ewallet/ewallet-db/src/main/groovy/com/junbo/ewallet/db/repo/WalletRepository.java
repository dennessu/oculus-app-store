/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.ewallet.db.repo;

import com.junbo.ewallet.common.def.WalletConst;
import com.junbo.ewallet.db.dao.LotTransactionDao;
import com.junbo.ewallet.db.dao.TransactionDao;
import com.junbo.ewallet.db.dao.WalletDao;
import com.junbo.ewallet.db.dao.WalletLotDao;
import com.junbo.ewallet.db.entity.LotTransactionEntity;
import com.junbo.ewallet.db.entity.TransactionEntity;
import com.junbo.ewallet.db.entity.WalletEntity;
import com.junbo.ewallet.db.entity.WalletLotEntity;
import com.junbo.ewallet.db.entity.def.NotEnoughMoneyException;
import com.junbo.ewallet.db.mapper.ModelMapper;
import com.junbo.ewallet.spec.def.TransactionType;
import com.junbo.ewallet.spec.def.WalletLotType;
import com.junbo.ewallet.spec.def.WalletType;
import com.junbo.ewallet.spec.model.*;
import com.junbo.oom.core.MappingContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.*;

/**
 * repo of wallet.
 */
public class WalletRepository {
    @Autowired
    private ModelMapper mapper;
    @Autowired
    private WalletDao walletDao;
    @Autowired
    private WalletLotDao walletLotDao;
    @Autowired
    private TransactionDao transactionDao;
    @Autowired
    private LotTransactionDao lotTransactionDao;
    //@Autowired
    //private TransactionSupport transactionSupport;

    public Wallet get(Long walletId) {
        return mapper.toWallet(walletDao.get(walletId), new MappingContext());
    }

    public Wallet get(Long userId, String type, String currency) {
        return mapper.toWallet(walletDao.get(userId, WalletType.valueOf(type),
                        currency == null ? null : currency), new MappingContext());
    }

    public Wallet create(Wallet wallet) {
        Long id = walletDao.insert(mapper.toWalletEntity(wallet, new MappingContext()));
        return get(id);
    }

    public Wallet update(Wallet wallet) {
        walletDao.update(mapper.toWalletEntity(wallet, new MappingContext()));
        return get(wallet.getId());
    }

    public Transaction credit(Wallet wallet, CreditRequest creditRequest) {
        wallet.setBalance(wallet.getBalance().add(creditRequest.getAmount()));
        walletDao.update(mapper.toWalletEntity(wallet, new MappingContext()));

        Long transactionId =
                transactionDao.insert(buildCreditTransaction(
                        creditRequest.getTrackingUuid(), wallet.getId(), creditRequest));

        Long walletLotId = walletLotDao.insert(buildWalletLot(wallet.getId(), creditRequest));
        lotTransactionDao.insert(buildCreditLotTransaction(walletLotDao.get(walletLotId), transactionId));

        return mapper.toTransaction(transactionDao.get(transactionId), new MappingContext());
    }

    public Transaction debit(Wallet wallet, DebitRequest debitRequest) {
        wallet.setBalance(wallet.getBalance().subtract(debitRequest.getAmount()));
        WalletEntity resultEntity = mapper.toWalletEntity(wallet, new MappingContext());
        walletDao.update(resultEntity);

        Long savedId =
                transactionDao.insert(buildDebitTransaction(
                        debitRequest.getTrackingUuid(), wallet.getId(), debitRequest));

        List<WalletLotEntity> lots = walletLotDao.getValidLot(wallet.getId());
        debit(resultEntity.getId(), lots, debitRequest.getAmount(), savedId);

        return mapper.toTransaction(transactionDao.get(savedId), new MappingContext());
    }

    private void debit(final Long walletId,
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

        /*
        if (sum.compareTo(BigDecimal.ZERO) > 0) {
            transactionSupport.executeInNewTransaction(new Callback() {
                @Override
                public void apply() {
                    BigDecimal validAmount = walletLotDao.getValidAmount(walletId);
                    WalletEntity entity = walletDao.get(walletId);
                    entity.setBalance(validAmount == null ? BigDecimal.ZERO : validAmount);
                    walletDao.update(entity);
                }
            });
            throw new NotEnoughMoneyException();
        }*/
        if (sum.compareTo(BigDecimal.ZERO) > 0) {
            throw new NotEnoughMoneyException();
        }
    }

    public Transaction refund(Wallet wallet, Long transactionId, RefundRequest refundRequest) {
        wallet.setBalance(wallet.getBalance().add(refundRequest.getAmount()));
        walletDao.update(mapper.toWalletEntity(wallet, new MappingContext()));
        TransactionEntity debitTransaction = transactionDao.get(transactionId);
        debitTransaction.setUnrefundedAmount(debitTransaction.getUnrefundedAmount().subtract(refundRequest.getAmount()));
        transactionDao.update(debitTransaction);

        Long savedId = transactionDao.insert(buildRefundTransaction(
                        refundRequest.getTrackingUuid(), wallet.getId(), refundRequest));

        List<LotTransactionEntity> lotTransactions = lotTransactionDao.getByTransactionId(transactionId);
        refund(lotTransactions, refundRequest.getAmount(), savedId);
        return mapper.toTransaction(transactionDao.get(savedId), new MappingContext());
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
        return mapper.toWallet(walletDao.getByTrackingUuid(shardMasterId, uuid), new MappingContext());
    }

    private WalletLotEntity buildWalletLot(Long walletId, CreditRequest creditRequest) {
        WalletLotEntity lotEntity = new WalletLotEntity();
        lotEntity.setWalletId(walletId);
        lotEntity.setTypeId(WalletLotType.valueOf(creditRequest.getCreditType()).getId());
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
        transactionEntity.setTypeId(TransactionType.CREDIT.getId());
        return transactionEntity;
    }

    private TransactionEntity buildDebitTransaction(UUID trackingUuid, Long walletId, DebitRequest debitRequest) {
        TransactionEntity transactionEntity = new TransactionEntity();
        transactionEntity.setTrackingUuid(trackingUuid);
        transactionEntity.setWalletId(walletId);
        transactionEntity.setAmount(debitRequest.getAmount());
        transactionEntity.setOfferId(debitRequest.getOfferId());
        transactionEntity.setTypeId(TransactionType.DEBIT.getId());
        transactionEntity.setUnrefundedAmount(debitRequest.getAmount());
        return transactionEntity;
    }

    private TransactionEntity buildRefundTransaction(UUID trackingUuid, Long walletId, RefundRequest refundRequest) {
        TransactionEntity transactionEntity = new TransactionEntity();
        transactionEntity.setTrackingUuid(trackingUuid);
        transactionEntity.setWalletId(walletId);
        transactionEntity.setAmount(refundRequest.getAmount());
        transactionEntity.setTypeId(TransactionType.REFUND.getId());
        return transactionEntity;
    }

    private LotTransactionEntity buildDebitLotTransaction(WalletLotEntity lotEntity, BigDecimal amount, Long transactionId) {
        LotTransactionEntity lotTransaction = new LotTransactionEntity();
        lotTransaction.setTransactionId(transactionId);
        lotTransaction.setTypeId(TransactionType.DEBIT.getId());
        lotTransaction.setWalletLotTypeId(lotEntity.getTypeId());
        lotTransaction.setWalletId(lotEntity.getWalletId());
        lotTransaction.setWalletLotId(lotEntity.getId());
        lotTransaction.setAmount(amount);
        lotTransaction.setUnrefundedAmount(amount);
        return lotTransaction;
    }

    private LotTransactionEntity buildCreditLotTransaction(WalletLotEntity lotEntity, Long transactionId) {
        LotTransactionEntity lotTransaction = new LotTransactionEntity();
        lotTransaction.setTransactionId(transactionId);
        lotTransaction.setTypeId(TransactionType.CREDIT.getId());
        lotTransaction.setWalletLotTypeId(lotEntity.getTypeId());
        lotTransaction.setWalletId(lotEntity.getWalletId());
        lotTransaction.setWalletLotId(lotEntity.getId());
        lotTransaction.setAmount(lotEntity.getRemainingAmount());
        return lotTransaction;
    }

    private LotTransactionEntity buildRefundLotTransaction(WalletLotEntity lotEntity, BigDecimal amount, Long transactionId) {
        LotTransactionEntity lotTransaction = new LotTransactionEntity();
        lotTransaction.setTransactionId(transactionId);
        lotTransaction.setTypeId(TransactionType.REFUND.getId());
        lotTransaction.setWalletLotTypeId(lotEntity.getTypeId());
        lotTransaction.setWalletId(lotEntity.getWalletId());
        lotTransaction.setWalletLotId(lotEntity.getId());
        lotTransaction.setAmount(amount);
        return lotTransaction;
    }

    public List<Wallet> getAll(long userId) {
        List<WalletEntity> list = walletDao.getAll(userId);
        List<Wallet> wallets = new ArrayList<>();
        for (WalletEntity entity : list) {
            wallets.add(mapper.toWallet(entity, new MappingContext()));
        }
        return wallets;
    }
}
