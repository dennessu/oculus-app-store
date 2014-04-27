/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.dao.impl;

import com.junbo.catalog.common.util.Action;
import com.junbo.catalog.common.util.EntityType;
import com.junbo.catalog.db.dao.ItemOfferRelationsDao;
import com.junbo.catalog.db.entity.ItemOfferRelationsEntity;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * Item DAO implementation.
 */
public class ItemOfferRelationsDaoImpl extends BaseDaoImpl<ItemOfferRelationsEntity> implements ItemOfferRelationsDao {
    @Override
    public List<ItemOfferRelationsEntity> getRelations(final Long entityId, final EntityType entityType) {
        return findAllBy(new Action<Criteria>() {
            @Override
            public void apply(Criteria criteria) {
                criteria.add(Restrictions.eq("entityType", entityType.getValue()));
                criteria.add(Restrictions.eq("entityId", entityId));
            }
        });
    }

    @Override
    public List<ItemOfferRelationsEntity> getRelations(final Long entityId,
                                                       final EntityType entityType,
                                                       final Long parentOfferId) {
        return findAllBy(new Action<Criteria>() {
            @Override
            public void apply(Criteria criteria) {
                criteria.add(Restrictions.eq("entityType", entityType.getValue()));
                criteria.add(Restrictions.eq("entityId", entityId));
                criteria.add(Restrictions.eq("parentOfferId", parentOfferId));
            }
        });
    }
}
