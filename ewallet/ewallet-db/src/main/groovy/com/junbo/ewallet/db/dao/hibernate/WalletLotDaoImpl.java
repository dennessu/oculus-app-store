/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.ewallet.db.dao.hibernate;

import com.junbo.ewallet.db.dao.WalletLotDao;
import com.junbo.ewallet.db.entity.LotTransactionEntity;
import com.junbo.ewallet.db.entity.WalletLotEntity;
import com.junbo.ewallet.db.entity.def.NotEnoughMoneyException;
import com.junbo.ewallet.db.entity.def.TransactionType;
import org.hibernate.CacheMode;
import org.hibernate.Query;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Hibernate impl of WalletLotDao.
 */
public class WalletLotDaoImpl extends BaseDao<WalletLotEntity> implements WalletLotDao {
    @Override
    public WalletLotEntity insert(WalletLotEntity lot, Long transactionId) {
        Date now = new Date();
        lot.setId(generateId(lot.getShardMasterId()));
        lot.setCreatedBy("DEFAULT"); //TODO
        lot.setCreatedTime(now);
        lot.setModifiedBy("DEFAULT");   //TODO
        lot.setModifiedTime(now);
        WalletLotEntity result = get((Long) currentSession().save(lot));
        currentSession().save(buildCreditLotTransaction(result, transactionId));
        return result;
    }

    @Override
    public void delete(Long id) {
        currentSession().delete(get(id));
    }

    @Override
    public void debit(Long walletId, BigDecimal sum, Long transactionId) {
        String queryString = "select * from ewallet_lot " +
                "where ewallet_id = (:walletId)" +
                " and remaining > 0" +
                " and (expiration_date is null or expiration_date >= (:now))" +
                " order by type desc";
        Query q = currentSession().createSQLQuery(queryString)
                .addEntity(WalletLotEntity.class)
                .setLong("walletId", walletId)
                .setDate("now", new Date());
        q.setCacheMode(CacheMode.IGNORE);
        ScrollableResults result = q.scroll(ScrollMode.FORWARD_ONLY);

        try {
            while (result.next()) {
                WalletLotEntity lot = (WalletLotEntity) result.get(0);
                BigDecimal remaining = lot.getRemainingAmount();
                if (sum.compareTo(remaining) <= 0) {
                    lot.setRemainingAmount(remaining.subtract(sum));
                    update(lot);
                    currentSession().save(buildDebitLotTransaction(lot, sum, transactionId));
                    return;
                } else {
                    lot.setRemainingAmount(BigDecimal.ZERO);
                    update(lot);
                    currentSession().save(buildDebitLotTransaction(lot, remaining, transactionId));
                }
                sum = sum.subtract(remaining);
            }
        } finally {
            result.close();
        }

        if (sum.compareTo(BigDecimal.ZERO) > 0) {
            throw new NotEnoughMoneyException();
        }
    }

    private LotTransactionEntity buildDebitLotTransaction(
            WalletLotEntity lotEntity, BigDecimal amount, Long transactionId) {
        LotTransactionEntity lotTransaction = new LotTransactionEntity();
        lotTransaction.setId(generateId(lotEntity.getId()));
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
        lotTransaction.setId(generateId(lotEntity.getId()));
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
