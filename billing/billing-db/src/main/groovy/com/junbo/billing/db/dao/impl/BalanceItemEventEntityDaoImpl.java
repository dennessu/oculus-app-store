/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.db.dao.impl;

import com.junbo.billing.db.BaseDao;
import com.junbo.billing.db.dao.BalanceItemEventEntityDao;
import com.junbo.billing.db.entity.BalanceItemEventEntity;
import org.hibernate.Session;

import java.util.Date;

/**
 * Created by xmchen on 14-4-17.
 */
public class BalanceItemEventEntityDaoImpl extends BaseDao implements BalanceItemEventEntityDao {
    @Override
    public BalanceItemEventEntity get(Long balanceItemEventId) {
        return (BalanceItemEventEntity)currentSession(balanceItemEventId).get(
                BalanceItemEventEntity.class, balanceItemEventId);
    }

    @Override
    public BalanceItemEventEntity save(BalanceItemEventEntity balanceItemEvent) {
        if (balanceItemEvent.getId() == null) {
            balanceItemEvent.setId(idGenerator.nextId(balanceItemEvent.getBalanceItemId()));
        }
        balanceItemEvent.setCreatedTime(new Date());
        if (balanceItemEvent.getCreatedBy() == null) {
            balanceItemEvent.setCreatedBy("0");
        }

        Session session = currentSession(balanceItemEvent.getId());
        session.save(balanceItemEvent);
        session.flush();
        return get(balanceItemEvent.getId());
    }

    @Override
    public BalanceItemEventEntity update(BalanceItemEventEntity balanceItemEvent, BalanceItemEventEntity oldBalanceItemEvent) {
        balanceItemEvent.setUpdatedTime(new Date());
        if (balanceItemEvent.getUpdatedBy() == null) {
            balanceItemEvent.setUpdatedBy("0");
        }

        Session session = currentSession(balanceItemEvent.getId());
        session.merge(balanceItemEvent);
        session.flush();

        return get(balanceItemEvent.getId());
    }
}
