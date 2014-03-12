/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.dao.impl;

import com.junbo.catalog.common.util.Action;
import com.junbo.catalog.db.dao.AttributeDao;
import com.junbo.catalog.db.entity.AttributeEntity;
import org.hibernate.Criteria;

import java.util.List;

/**
 * Offer DAO implementation.
 */
public class AttributeDaoImpl extends BaseDaoImpl<AttributeEntity> implements AttributeDao {
    public List<AttributeEntity> getAttributes(final int start, final int size) {
        return findAllBy(new Action<Criteria>() {
            public void apply(Criteria criteria) {
                criteria.setFirstResult(start);
                criteria.setFetchSize(size);
            }
        });
    }
}
