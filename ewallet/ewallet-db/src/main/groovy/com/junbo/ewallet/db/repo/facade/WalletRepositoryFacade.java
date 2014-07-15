/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.ewallet.db.repo.facade;

import com.junbo.ewallet.common.def.WalletConst;
import com.junbo.ewallet.db.entity.def.NotEnoughMoneyException;
import com.junbo.ewallet.db.repo.LotTransactionRepository;
import com.junbo.ewallet.db.repo.TransactionRepository;
import com.junbo.ewallet.db.repo.WalletLotRepository;
import com.junbo.ewallet.db.repo.WalletRepository;
import com.junbo.ewallet.spec.def.TransactionType;
import com.junbo.ewallet.spec.def.WalletType;
import com.junbo.ewallet.spec.model.*;
import com.junbo.langur.core.promise.SyncModeScope;
import org.springframework.beans.factory.annotation.Required;

import java.math.BigDecimal;
import java.util.*;

/**
 * repo of wallet.
 */
public class WalletRepositoryFacade {
    private WalletRepository walletRepository;
    private WalletLotRepository walletLotRepository;
    private TransactionRepository transactionRepository;
    private LotTransactionRepository lotTransactionRepository;

    @Required
    public void setWalletRepository(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    @Required
    public void setWalletLotRepository(WalletLotRepository walletLotRepository) {
        this.walletLotRepository = walletLotRepository;
    }

    @Required
    public void setTransactionRepository(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Required
    public void setLotTransactionRepository(LotTransactionRepository lotTransactionRepository) {
        this.lotTransactionRepository = lotTransactionRepository;
    }

    public Wallet get(Long walletId) {
        try (SyncModeScope scope = new SyncModeScope()) {
            return walletRepository.get(walletId).syncGet();
        }
    }

    public Wallet get(Long userId, String type, String currency) {
        try (SyncModeScope scope = new SyncModeScope()) {
            return walletRepository.get(userId, WalletType.valueOf(type), currency == null ? null : currency).syncGet();
        }
    }

    public Wallet create(Wallet wallet) {
        try (SyncModeScope scope = new SyncModeScope()) {
            return walletRepository.create(wallet).syncGet();
        }
    }

    public Wallet update(Wallet wallet) {
        try (SyncModeScope scope = new SyncModeScope()) {
            return walletRepository.update(wallet).syncGet();
        }
    }

    public Transaction credit(Wallet wallet, CreditRequest creditRequest) {
        try (SyncModeScope scope = new SyncModeScope()) {
            wallet.setBalance(wallet.getBalance().add(creditRequest.getAmount()));
            walletRepository.update(wallet).syncGet();

            Transaction transaction = transactionRepository.create(buildCreditTransaction(wallet.getId(), creditRequest)).syncGet();
            WalletLot walletLot = walletLotRepository.create(buildWalletLot(wallet.getId(), creditRequest)).syncGet();
            lotTransactionRepository.create(buildCreditLotTransaction(walletLot, transaction.getId())).syncGet();

            return transaction;
        }
    }

    public Transaction debit(Wallet wallet, DebitRequest debitRequest) {
        try (SyncModeScope scope = new SyncModeScope()) {
            wallet.setBalance(wallet.getBalance().subtract(debitRequest.getAmount()));
            walletRepository.update(wallet).syncGet();

            Transaction transaction = transactionRepository.create(buildDebitTransaction(wallet.getId(), debitRequest)).syncGet();

            List<WalletLot> walletLots = walletLotRepository.getValidLot(wallet.getId()).syncGet();
            debit(walletLots, debitRequest.getAmount(), transaction.getId());

            return transaction;
        }
    }

    public void correctBalance(Long walletId){
        try (SyncModeScope scope = new SyncModeScope()) {
            BigDecimal validAmount = walletLotRepository.getValidAmount(walletId).syncGet();
            Wallet wallet = walletRepository.get(walletId).syncGet();
            wallet.setBalance(validAmount == null ? BigDecimal.ZERO : validAmount);
            walletRepository.update(wallet);
        }
    }

    public Transaction refund(Wallet wallet, Long transactionId, RefundRequest refundRequest) {
        try (SyncModeScope scope = new SyncModeScope()) {
            wallet.setBalance(wallet.getBalance().add(refundRequest.getAmount()));
            walletRepository.update(wallet).syncGet();

            Transaction debitTransaction = transactionRepository.get(transactionId).syncGet();
            debitTransaction.setUnrefundedAmount(debitTransaction.getUnrefundedAmount().subtract(refundRequest.getAmount()));
            transactionRepository.update(debitTransaction).syncGet();

            Transaction savedTrans = transactionRepository.create(buildRefundTransaction(wallet.getId(), refundRequest)).syncGet();

            List<LotTransaction> lotTransactions = lotTransactionRepository.getByTransactionId(transactionId).syncGet();
            refund(lotTransactions, refundRequest.getAmount(), savedTrans.getId());

            return savedTrans;
        }
    }

    public Wallet getByTrackingUuid(Long shardMasterId, UUID uuid) {
        try (SyncModeScope scope = new SyncModeScope()) {
            return walletRepository.getByTrackingUuid(shardMasterId, uuid).syncGet();
        }
    }

    public List<Wallet> getAll(long userId) {
        try (SyncModeScope scope = new SyncModeScope()) {
            return walletRepository.getAll(userId).syncGet();
        }
    }

    private void debit(final List<WalletLot> walletLots, BigDecimal sum, Long transactionId) {
        try (SyncModeScope scope = new SyncModeScope()) {
            Collections.sort(walletLots, new Comparator<WalletLot>() {
                @Override
                public int compare(WalletLot o1, WalletLot o2) {
                    return o1.getRemainingAmount().compareTo(o2.getRemainingAmount());
                }
            });

            for (int i = 0; i < walletLots.size(); i++) {
                WalletLot walletLot = walletLots.get(i);
                BigDecimal remaining = walletLot.getRemainingAmount();
                if (sum.compareTo(remaining) <= 0) {
                    walletLot.setRemainingAmount(remaining.subtract(sum));
                    walletLot = walletLotRepository.update(walletLot).syncGet();
                    lotTransactionRepository.create(buildDebitLotTransaction(walletLot, sum, transactionId)).syncGet();
                    return;
                } else {
                    BigDecimal remainingAmount = walletLot.getRemainingAmount();
                    walletLot.setRemainingAmount(BigDecimal.ZERO);
                    walletLot = walletLotRepository.update(walletLot).syncGet();
                    lotTransactionRepository.create(buildDebitLotTransaction(walletLot, remainingAmount, transactionId)).syncGet();
                }
                sum = sum.subtract(remaining);
            }

            if (sum.compareTo(BigDecimal.ZERO) > 0) {
                throw new NotEnoughMoneyException();
            }
        }
    }

    private void refund(List<LotTransaction> lotTransactions, BigDecimal amount, Long transactionId) {
        try (SyncModeScope scope = new SyncModeScope()) {
            Date now = new Date();
            Boolean refundEnded = false;
            for (LotTransaction lotTransaction : lotTransactions) {
                WalletLot lot = walletLotRepository.get(lotTransaction.getWalletLotId()).syncGet();
                if (amount.subtract(lotTransaction.getUnrefundedAmount()).compareTo(BigDecimal.ZERO) <= 0) {
                    lot.setRemainingAmount(lot.getRemainingAmount().add(amount));
                    lotTransaction.setUnrefundedAmount(lotTransaction.getUnrefundedAmount().subtract(amount));
                    lotTransactionRepository.create(buildRefundLotTransaction(lot, amount, transactionId));
                    refundEnded = true;
                } else {
                    lot.setRemainingAmount(lot.getRemainingAmount().add(lotTransaction.getUnrefundedAmount()));
                    lotTransactionRepository.create(buildRefundLotTransaction(lot, lotTransaction.getUnrefundedAmount(), transactionId));
                    amount = amount.subtract(lotTransaction.getUnrefundedAmount());
                    lotTransaction.setUnrefundedAmount(BigDecimal.ZERO);
                }

                if (lot.getExpirationDate() != null && lot.getExpirationDate().before(now)) {
                    lot.setExpirationDate(WalletConst.NEVER_EXPIRE);  //enable lot
                }
                walletLotRepository.update(lot);
                lotTransactionRepository.update(lotTransaction);
                if (refundEnded) {
                    break;
                }
            }
        }
    }

    private WalletLot buildWalletLot(Long walletId, CreditRequest creditRequest) {
        WalletLot lot = new WalletLot();
        lot.setWalletId(walletId);
        lot.setType(creditRequest.getCreditType());
        lot.setTotalAmount(creditRequest.getAmount());
        lot.setRemainingAmount(creditRequest.getAmount());
        lot.setExpirationDate(creditRequest.getExpirationDate() == null ? WalletConst.NEVER_EXPIRE : creditRequest.getExpirationDate());
        return lot;
    }

    private Transaction buildCreditTransaction(Long walletId, CreditRequest creditRequest) {
        Transaction transaction = new Transaction();
        transaction.setWalletId(walletId);
        transaction.setTrackingUuid(creditRequest.getTrackingUuid());
        transaction.setAmount(creditRequest.getAmount());
        transaction.setOfferId(creditRequest.getOfferId());
        transaction.setType(TransactionType.CREDIT.toString());
        return transaction;
    }

    private LotTransaction buildCreditLotTransaction(WalletLot walletLot, Long transactionId) {
        LotTransaction lotTransaction = new LotTransaction();
        lotTransaction.setTransactionId(transactionId);
        lotTransaction.setType(TransactionType.CREDIT.toString());
        lotTransaction.setWalletLotType(walletLot.getType());
        lotTransaction.setWalletId(walletLot.getWalletId());
        lotTransaction.setWalletLotId(walletLot.getId());
        lotTransaction.setAmount(walletLot.getRemainingAmount());
        return lotTransaction;
    }

    private Transaction buildDebitTransaction(Long walletId, DebitRequest debitRequest) {
        Transaction transaction = new Transaction();
        transaction.setTrackingUuid(debitRequest.getTrackingUuid());
        transaction.setWalletId(walletId);
        transaction.setAmount(debitRequest.getAmount());
        transaction.setOfferId(debitRequest.getOfferId());
        transaction.setType(TransactionType.DEBIT.toString());
        transaction.setUnrefundedAmount(debitRequest.getAmount());
        return transaction;
    }

    private Transaction buildRefundTransaction(Long walletId, RefundRequest refundRequest) {
        Transaction transaction = new Transaction();
        transaction.setTrackingUuid(refundRequest.getTrackingUuid());
        transaction.setWalletId(walletId);
        transaction.setAmount(refundRequest.getAmount());
        transaction.setType(TransactionType.REFUND.toString());
        return transaction;
    }

    private LotTransaction buildDebitLotTransaction(WalletLot walletLot, BigDecimal amount, Long transactionId) {
        LotTransaction lotTransaction = new LotTransaction();
        lotTransaction.setTransactionId(transactionId);
        lotTransaction.setType(TransactionType.DEBIT.toString());
        lotTransaction.setWalletLotType(walletLot.getType());
        lotTransaction.setWalletId(walletLot.getWalletId());
        lotTransaction.setWalletLotId(walletLot.getId());
        lotTransaction.setAmount(amount);
        lotTransaction.setUnrefundedAmount(amount);
        return lotTransaction;
    }

    private LotTransaction buildRefundLotTransaction(WalletLot walletLot, BigDecimal amount, Long transactionId) {
        LotTransaction lotTransaction = new LotTransaction();
        lotTransaction.setTransactionId(transactionId);
        lotTransaction.setType(TransactionType.REFUND.toString());
        lotTransaction.setWalletLotType(walletLot.getType());
        lotTransaction.setWalletId(walletLot.getWalletId());
        lotTransaction.setWalletLotId(walletLot.getId());
        lotTransaction.setAmount(amount);
        return lotTransaction;
    }
}
