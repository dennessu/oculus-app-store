/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.dao.impl;

import com.junbo.catalog.common.util.Action;
import com.junbo.catalog.db.dao.ItemDao;
import com.junbo.catalog.db.entity.ItemEntity;
import com.junbo.catalog.spec.model.item.ItemsGetOptions;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * Item DAO implementation.
 */
public class ItemDaoImpl extends BaseDaoImpl<ItemEntity> implements ItemDao {
    @Override
    public List<ItemEntity> getItems(final ItemsGetOptions options) {
        return findAllBy(new Action<Criteria>() {
            @Override
            public void apply(Criteria criteria) {
                addIdRestriction("itemId", options.getItemIds(), criteria);
                if (options.getCurated() != null) {
                    criteria.add(Restrictions.eq("curated", options.getCurated()));
                }
                if (options.getGenre() != null) {
                    criteria.add(Restrictions.sqlRestriction(options.getGenre() + "=ANY(genres)"));
                }
                options.ensurePagingValid();
                criteria.setFirstResult(options.getStart()).setMaxResults(options.getSize());
            }
        });
    }
}
