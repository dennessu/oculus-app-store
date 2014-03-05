/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.dao.impl;

import com.junbo.catalog.common.util.Action;
import com.junbo.catalog.db.dao.CategoryDraftDao;
import com.junbo.catalog.db.entity.CategoryDraftEntity;
import org.hibernate.Criteria;

import java.util.List;

/**
 * Category draft DAO implementation.
 */
public class CategoryDraftDaoImpl extends BaseDaoImpl<CategoryDraftEntity> implements CategoryDraftDao {
    @Override
    public List<CategoryDraftEntity> getCategories(final int start, final int size) {
        return findAllBy(new Action<Criteria>() {
            public void apply(Criteria criteria) {
                criteria.setFirstResult(start);
                criteria.setFetchSize(size);
                //criteria.add(Restrictions.eq("status", "PUBLISHED"));
            }
        });
    }
}
