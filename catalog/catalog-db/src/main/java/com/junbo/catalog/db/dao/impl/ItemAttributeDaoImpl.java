/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.dao.impl;

import com.junbo.catalog.common.util.Action;
import com.junbo.catalog.db.dao.ItemAttributeDao;
import com.junbo.catalog.db.entity.ItemAttributeEntity;
import com.junbo.catalog.spec.model.attribute.ItemAttributesGetOptions;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * Item Attribute DAO implementation.
 */
public class ItemAttributeDaoImpl extends BaseDaoImpl<ItemAttributeEntity> implements ItemAttributeDao {
    public List<ItemAttributeEntity> getAttributes(final ItemAttributesGetOptions options) {
        return findAllBy(new Action<Criteria>() {
            public void apply(Criteria criteria) {
                addIdRestriction("id", options.getAttributeIds(), criteria);
                if (options.getAttributeType() != null) {
                    criteria.add(Restrictions.eq("type", options.getAttributeType()));
                }
                options.ensurePagingValid();
                criteria.setFirstResult(options.getStart()).setMaxResults(options.getSize());
            }
        });
    }
}
