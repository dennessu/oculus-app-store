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

        balanceItem.setBalanceItemId(idGenerator.nextId(balanceItem.getBalanceId()));

        Session session = currentSession(balanceItem.getBalanceItemId());
        session.save(balanceItem);
        session.flush();
        return get(balanceItem.getBalanceItemId());
    }

    @Override
    public BalanceItemEntity update(BalanceItemEntity balanceItem) {

        Session session = currentSession(balanceItem.getBalanceItemId());
        session.merge(balanceItem);
        session.flush();

        return get(balanceItem.getBalanceItemId());
    }

    public List<BalanceItemEntity> findByBalanceId(Long balanceId) {
        Criteria criteria = currentSession(balanceId).createCriteria(BalanceItemEntity.class).
                add(Restrictions.eq("balanceId", balanceId));
        return criteria.list();
    }
}
