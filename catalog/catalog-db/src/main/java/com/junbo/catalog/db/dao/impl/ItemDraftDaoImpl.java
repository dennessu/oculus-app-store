/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.dao.impl;

import com.junbo.catalog.common.util.Action;
import com.junbo.catalog.db.dao.ItemDraftDao;
import com.junbo.catalog.db.entity.ItemDraftEntity;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Item draft DAO implementation.
 */
public class ItemDraftDaoImpl extends VersionedDaoImpl<ItemDraftEntity> implements ItemDraftDao {
    @Override
    public List<ItemDraftEntity> getItems(final int start, final int size, final String status) {
        return findAllBy(new Action<Criteria>() {
            public void apply(Criteria criteria) {
                criteria.setFirstResult(start);
                criteria.setFetchSize(size);
                if (!StringUtils.isEmpty(status)) {
                    criteria.add(Restrictions.eq("status", status));
                }
            }
        });
    }
}
