/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.dao.impl;

import com.junbo.catalog.common.util.Action;
import com.junbo.catalog.db.dao.PriceTierDao;
import com.junbo.catalog.db.entity.PriceTierEntity;
import org.hibernate.Criteria;

import java.util.List;

/**
 * Price tier DAO implementation.
 */
public class PriceTierDaoImpl extends BaseDaoImpl<PriceTierEntity> implements PriceTierDao {
    public List<PriceTierEntity> getPriceTiers(final int start, final int size) {
        return findAllBy(new Action<Criteria>() {
            public void apply(Criteria criteria) {
                criteria.setFirstResult(start);
                criteria.setFetchSize(size);
            }
        });
    }
}
