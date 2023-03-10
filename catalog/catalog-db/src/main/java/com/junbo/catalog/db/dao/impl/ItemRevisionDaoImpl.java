/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.dao.impl;

import com.junbo.catalog.common.util.Action;
import com.junbo.catalog.db.dao.ItemRevisionDao;
import com.junbo.catalog.db.entity.ItemRevisionEntity;
import com.junbo.catalog.spec.enums.Status;
import com.junbo.catalog.spec.model.item.ItemRevisionsGetOptions;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Item revision DAO implementation.
 */
public class ItemRevisionDaoImpl extends BaseDaoImpl<ItemRevisionEntity> implements ItemRevisionDao {
    @Override
    public List<ItemRevisionEntity> getRevisions(final ItemRevisionsGetOptions options) {
        return findAllBy(new Action<Criteria>() {
            @Override
            public void apply(Criteria criteria) {
                addIdRestriction("itemId", options.getItemIds(), criteria);
                addIdRestriction("revisionId", options.getRevisionIds(), criteria);
                if (!StringUtils.isEmpty(options.getStatus())) {
                    criteria.add(Restrictions.eq("status", options.getStatus()));
                }
                criteria.setFirstResult(options.getValidStart()).setMaxResults(options.getValidSize());
            }
        });
    }

    public ItemRevisionEntity getRevision(final String itemId, final Long timestamp) {
        return findBy(new Action<Criteria>() {
            @Override
            public void apply(Criteria criteria) {
                criteria.add(Restrictions.eq("itemId", itemId));
                criteria.add(Restrictions.le("timestamp", timestamp));
                criteria.addOrder(Order.desc("timestamp"));
                criteria.setMaxResults(1);
            }
        });
    }

    public List<ItemRevisionEntity> getRevisions(final String hostItemId) {
        return findAllBy(new Action<Criteria>() {
            @Override
            public void apply(Criteria criteria) {
                criteria.add(Restrictions.sqlRestriction(hostItemId + "=ANY(host_item_ids)"));
                criteria.add(Restrictions.eq("status", Status.APPROVED.name()));
            }
        });
    }
}
