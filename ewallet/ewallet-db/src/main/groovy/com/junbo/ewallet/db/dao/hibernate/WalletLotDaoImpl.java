/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.ewallet.db.dao.hibernate;

import com.junbo.ewallet.db.dao.WalletLotDao;
import com.junbo.ewallet.db.entity.hibernate.WalletLotEntity;
import org.hibernate.Query;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Hibernate impl of WalletLotDao.
 */
public class WalletLotDaoImpl extends BaseDao<WalletLotEntity> implements WalletLotDao{
    @Override
    public void delete(Long id) {
        currentSession().delete(get(id));
    }

    @Override
    public void debit(Long walletId, BigDecimal sum) {
        String queryString = "select * from ewallet_lot " +
                "where ewallet_id = (:walletId)" +
                " and remaining > money(0)" +
                " and (expiration_date is null or expiration_date <= (:now))" +
                " order by type desc";
        Query q = currentSession().createSQLQuery(queryString)
                .addEntity(WalletLotEntity.class)
                .setLong("walletId", walletId)
                .setDate("now", new Date());
        ScrollableResults result = q.scroll(ScrollMode.FORWARD_ONLY);

        while(result.next()){
            WalletLotEntity lot = (WalletLotEntity) result.get(0);
            sum = sum.subtract(lot.getRemainingAmount());
            if(sum.compareTo(BigDecimal.ZERO) <= 0){
                lot.setRemainingAmount(lot.getRemainingAmount().add(sum));
                update(lot);
                return;
            }else {
                lot.setRemainingAmount(BigDecimal.ZERO);
                update(lot);
            }
        }
        if(sum.compareTo(BigDecimal.ZERO) > 0){
            //TODO : build own exception later
            throw new RuntimeException("not enough money!");
        }
    }
}
