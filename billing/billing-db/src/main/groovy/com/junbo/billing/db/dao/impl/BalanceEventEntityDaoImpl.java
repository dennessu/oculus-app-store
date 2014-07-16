/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.db.dao.impl;

import com.junbo.billing.db.BaseDao;
import com.junbo.billing.db.entity.BalanceEventEntity;
import com.junbo.billing.db.dao.BalanceEventEntityDao;
import org.hibernate.Session;

import java.util.Date;

/**
 * Created by xmchen on 14-1-21.
 */
public class BalanceEventEntityDaoImpl extends BaseDao implements BalanceEventEntityDao {
    @Override
    public BalanceEventEntity get(Long balanceEventId) {
        return (BalanceEventEntity)currentSession(balanceEventId).get(BalanceEventEntity.class, balanceEventId);
    }

    @Override
    public BalanceEventEntity save(BalanceEventEntity balanceEvent) {
        if (balanceEvent.getId() == null) {
            balanceEvent.setId(idGenerator.nextId(balanceEvent.getBalanceId()));
        }
        balanceEvent.setCreatedTime(new Date());
        if (balanceEvent.getCreatedBy() == null) {
            balanceEvent.setCreatedBy("0");
        }
        Session session = currentSession(balanceEvent.getId());
        session.save(balanceEvent);
        session.flush();
        return get(balanceEvent.getId());
    }

    @Override
    public BalanceEventEntity update(BalanceEventEntity balanceEvent, BalanceEventEntity oldBalanceEvent) {
        balanceEvent.setUpdatedTime(new Date());
        if (balanceEvent.getUpdatedBy() == null) {
            balanceEvent.setUpdatedBy("0");
        }

        Session session = currentSession(balanceEvent.getId());
        session.merge(balanceEvent);
        session.flush();
        return get(balanceEvent.getId());
    }
}
