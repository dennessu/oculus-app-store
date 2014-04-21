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

        balanceItemEvent.setEventId(idGenerator.nextId(balanceItemEvent.getBalanceItemId()));

        Session session = currentSession(balanceItemEvent.getEventId());
        session.save(balanceItemEvent);
        session.flush();
        return get(balanceItemEvent.getEventId());
    }

    @Override
    public BalanceItemEventEntity update(BalanceItemEventEntity balanceItemEvent) {

        Session session = currentSession(balanceItemEvent.getEventId());
        session.merge(balanceItemEvent);
        session.flush();

        return get(balanceItemEvent.getEventId());
    }
}
