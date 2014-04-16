/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.catalog.db.dao.impl;

import com.junbo.catalog.db.dao.EntitlementTypeDao;
import com.junbo.catalog.db.entity.EntitlementTypeEntity;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * entitlementType Dao impl.
 */
public class EntitlementTypeDaoImpl implements EntitlementTypeDao{
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public List<EntitlementTypeEntity> getAll() {
        return sessionFactory.getCurrentSession().createQuery("from EntitlementTypeEntity").list();
    }
}
