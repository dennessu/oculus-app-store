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
        return walletRepository.get(walletId).get();
    }

    public Wallet get(Long userId, String type, String currency) {
        return walletRepository.get(userId, WalletType.valueOf(type), currency == null ? null : currency).get();
    }

    public Wallet create(Wallet wallet) {
        return walletRepository.create(wallet).get();
    }

    public Wallet update(Wallet wallet, Wallet oldWallet) {
        return walletRepository.update(wallet, oldWallet).get();
    }

    public Transaction credit(Wallet wallet, CreditRequest creditRequest) {
        wallet.setBalance(wallet.getBalance().add(creditRequest.getAmount()));
        walletRepository.update(wallet, wallet).get();

        Transaction transaction = transactionRepository.create(buildCreditTransaction(wallet.getId(), creditRequest)).get();
        WalletLot walletLot = walletLotRepository.create(buildWalletLot(wallet.getId(), creditRequest)).get();
        lotTransactionRepository.create(buildCreditLotTransaction(walletLot, transaction.getId())).get();

        return transaction;
    }

    public Transaction debit(Wallet wallet, DebitRequest debitRequest) {
        wallet.setBalance(wallet.getBalance().subtract(debitRequest.getAmount()));
        walletRepository.update(wallet, wallet).get();

        Transaction transaction = transactionRepository.create(buildDebitTransaction(wallet.getId(), debitRequest)).get();

        List<WalletLot> walletLots = walletLotRepository.getValidLot(wallet.getId()).get();
        debit(walletLots, debitRequest.getAmount(), transaction.getId());

        return transaction;
    }

    public void correctBalance(Long walletId){
        BigDecimal validAmount = walletLotRepository.getValidAmount(walletId).get();
        Wallet wallet = walletRepository.get(walletId).get();
        wallet.setBalance(validAmount == null ? BigDecimal.ZERO : validAmount);
        walletRepository.update(wallet, wallet);
    }

    public Transaction refund(Wallet wallet, Long transactionId, RefundRequest refundRequest) {
        wallet.setBalance(wallet.getBalance().add(refundRequest.getAmount()));
        walletRepository.update(wallet, wallet).get();

        Transaction debitTransaction = transactionRepository.get(transactionId).get();
        debitTransaction.setUnrefundedAmount(debitTransaction.getUnrefundedAmount().subtract(refundRequest.getAmount()));
        transactionRepository.update(debitTransaction, debitTransaction).get();

        Transaction savedTrans = transactionRepository.create(buildRefundTransaction(wallet.getId(), refundRequest)).get();

        List<LotTransaction> lotTransactions = lotTransactionRepository.getByTransactionId(transactionId).get();
        refund(lotTransactions, refundRequest.getAmount(), savedTrans.getId());

        return savedTrans;
    }

    public Wallet getByTrackingUuid(Long shardMasterId, UUID uuid) {
        return walletRepository.getByTrackingUuid(shardMasterId, uuid).get();
    }

    public List<Wallet> getAll(long userId) {
        return walletRepository.getAll(userId).get();
    }

    private void debit(final List<WalletLot> walletLots, BigDecimal sum, Long transactionId) {
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
                walletLot = walletLotRepository.update(walletLot, walletLot).get();
                lotTransactionRepository.create(buildDebitLotTransaction(walletLot, sum, transactionId)).get();
                return;
            } else {
                BigDecimal remainingAmount = walletLot.getRemainingAmount();
                walletLot.setRemainingAmount(BigDecimal.ZERO);
                walletLot = walletLotRepository.update(walletLot, walletLot).get();
                lotTransactionRepository.create(buildDebitLotTransaction(walletLot, remainingAmount, transactionId)).get();
            }
            sum = sum.subtract(remaining);
        }

        if (sum.compareTo(BigDecimal.ZERO) > 0) {
            throw new NotEnoughMoneyException();
        }
    }

    private void refund(List<LotTransaction> lotTransactions, BigDecimal amount, Long transactionId) {
        Date now = new Date();
        Boolean refundEnded = false;
        for (LotTransaction lotTransaction : lotTransactions) {
            WalletLot lot = walletLotRepository.get(lotTransaction.getWalletLotId()).get();
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
            walletLotRepository.update(lot, lot);
            lotTransactionRepository.update(lotTransaction, lotTransaction);
            if (refundEnded) {
                break;
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
