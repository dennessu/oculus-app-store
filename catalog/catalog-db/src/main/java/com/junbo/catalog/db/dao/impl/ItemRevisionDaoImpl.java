/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.dao.impl;

import com.junbo.catalog.common.util.Action;
import com.junbo.catalog.db.dao.ItemRevisionDao;
import com.junbo.catalog.db.entity.ItemRevisionEntity;
import com.junbo.catalog.spec.model.item.ItemRevisionsGetOptions;
import org.hibernate.Criteria;
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
                if (!StringUtils.isEmpty(options.getType())) {
                    criteria.add(Restrictions.eq("type", options.getType()));
                }
                options.ensurePagingValid();
                criteria.setFirstResult(options.getStart()).setMaxResults(options.getSize());
            }
        });
    }
}
