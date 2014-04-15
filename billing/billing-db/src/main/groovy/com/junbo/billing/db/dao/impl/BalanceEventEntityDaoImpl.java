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

        balanceEvent.setEventId(idGenerator.nextId(balanceEvent.getBalanceId()));

        Session session = currentSession(balanceEvent.getEventId());
        session.save(balanceEvent);
        session.flush();
        return get(balanceEvent.getEventId());
    }

    @Override
    public BalanceEventEntity update(BalanceEventEntity balanceEvent) {

        Session session = currentSession(balanceEvent.getEventId());
        session.merge(balanceEvent);
        session.flush();

        return get(balanceEvent.getEventId());
    }
}
