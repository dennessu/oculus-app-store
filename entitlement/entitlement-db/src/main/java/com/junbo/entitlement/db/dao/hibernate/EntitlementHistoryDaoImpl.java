/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.db.dao.hibernate;

import com.junbo.common.id.EntitlementId;
import com.junbo.entitlement.db.dao.EntitlementHistoryDao;
import com.junbo.entitlement.db.entity.EntitlementHistoryEntity;
import com.junbo.sharding.IdGeneratorFacade;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Hibernate Impl of EntitlementHistory Dao.
 */
@Component
public class EntitlementHistoryDaoImpl implements EntitlementHistoryDao {
    @Autowired
    private IdGeneratorFacade idGenerator;

    private SessionFactory sessionFactory;

    @Override
    public void insert(EntitlementHistoryEntity entitlementHistory) {
        entitlementHistory.setEntitlementHistoryId(
                idGenerator.nextId(EntitlementId.class, entitlementHistory.getEntitlementId()));
        sessionFactory.getCurrentSession().save(entitlementHistory);
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}
