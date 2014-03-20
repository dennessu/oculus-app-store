/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.dao.impl;

import com.junbo.catalog.common.util.Action;
import com.junbo.catalog.common.util.Utils;
import com.junbo.catalog.db.dao.VersionedDao;
import com.junbo.catalog.db.entity.VersionedEntity;
import com.junbo.catalog.spec.model.common.Status;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 * Base DAO implementation.
 * @param <T> entity to operate.
 */
public abstract class VersionedDaoImpl<T extends VersionedEntity> extends BaseDaoImpl<T> implements VersionedDao<T> {

    public Long create(T entity) {
        entity.setTimestamp(Utils.currentTimestamp());
        return super.create(entity);
    }

    protected T get(final Long id, final Long timestamp, final String idPropertyName) {
        T entity = findBy(new Action<Criteria>() {
            public void apply(Criteria criteria) {
                criteria.add(Restrictions.eq(idPropertyName, id));
                if (timestamp != null) {
                    criteria.add(Restrictions.le("timestamp", timestamp));
                }
                criteria.addOrder(Order.desc("timestamp"));
                criteria.setMaxResults(1);
            }
        });

        if (entity!=null && Status.DELETED.equalsIgnoreCase(entity.getStatus())) {
            entity = null;
        }

        return entity;
    }

    public Long update(T entity) {
        entity.setTimestamp(Utils.currentTimestamp());
        return super.update(entity);
    }
}
