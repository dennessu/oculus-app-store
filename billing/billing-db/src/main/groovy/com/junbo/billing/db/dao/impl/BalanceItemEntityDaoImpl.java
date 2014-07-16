/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.db.dao.impl;

import com.junbo.billing.db.BaseDao;
import com.junbo.billing.db.entity.BalanceItemEntity;
import com.junbo.billing.db.dao.BalanceItemEntityDao;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.util.Date;
import java.util.List;

/**
 * Created by xmchen on 14-1-21.
 */
@SuppressWarnings("unchecked")
public class BalanceItemEntityDaoImpl extends BaseDao implements BalanceItemEntityDao {
    @Override
    public BalanceItemEntity get(Long balanceItemId) {
        return (BalanceItemEntity)currentSession(balanceItemId).get(BalanceItemEntity.class, balanceItemId);
    }

    @Override
    public BalanceItemEntity save(BalanceItemEntity balanceItem) {
        if (balanceItem.getId() == null) {
            balanceItem.setId(idGenerator.nextId(balanceItem.getBalanceId()));
        }
        balanceItem.setCreatedTime(new Date());
        if (balanceItem.getCreatedBy() == null) {
            balanceItem.setCreatedBy("0");
        }

        Session session = currentSession(balanceItem.getId());
        session.save(balanceItem);
        session.flush();
        return get(balanceItem.getId());
    }

    @Override
    public BalanceItemEntity update(BalanceItemEntity balanceItem, BalanceItemEntity oldBalanceItem) {
        balanceItem.setUpdatedTime(new Date());
        if (balanceItem.getUpdatedBy() == null) {
            balanceItem.setUpdatedBy("0");
        }

        Session session = currentSession(balanceItem.getId());
        session.merge(balanceItem);
        session.flush();
        return get(balanceItem.getId());
    }

    public List<BalanceItemEntity> findByBalanceId(Long balanceId) {
        Criteria criteria = currentSession(balanceId).createCriteria(BalanceItemEntity.class).
                add(Restrictions.eq("balanceId", balanceId));
        return criteria.list();
    }
}
