/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.dao.impl;

import com.junbo.catalog.common.util.Action;
import com.junbo.catalog.db.dao.ItemDao;
import com.junbo.catalog.db.entity.ItemEntity;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

/**
 * Item DAO implementation.
 */
public class ItemDaoImpl extends BaseDaoImpl<ItemEntity> implements ItemDao {
    @Override
    public ItemEntity getItem(final long itemId, final int revision) {
        return findBy(new Action<Criteria>() {
            public void apply(Criteria criteria) {
                criteria.add(Restrictions.eq("item_id", itemId));
                criteria.add(Restrictions.eq("revision", revision));
            }
        });
    }

    /*@Override
    public List<ItemEntity> getItems(final long itemId, final int start, final int size) {
        return findAllBy(new Action<Criteria>() {
            public void apply(Criteria criteria) {
                criteria.setFirstResult(start);
                criteria.setFetchSize(size);
                criteria.add(Restrictions.eq("item_id", itemId));
            }
        });
    }*/
}
