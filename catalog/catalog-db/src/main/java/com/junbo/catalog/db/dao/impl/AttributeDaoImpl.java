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
import org.hibernate.criterion.Restrictions;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Offer DAO implementation.
 */
public class AttributeDaoImpl extends BaseDaoImpl<AttributeEntity> implements AttributeDao {
    public List<AttributeEntity> getAttributes(final int start, final int size, final String attributeType) {
        return findAllBy(new Action<Criteria>() {
            public void apply(Criteria criteria) {
                criteria.setFirstResult(start);
                criteria.setFetchSize(size);
                if (!StringUtils.isEmpty(attributeType)) {
                    criteria.add(Restrictions.eq("type", attributeType));
                }
            }
        });
    }
}
