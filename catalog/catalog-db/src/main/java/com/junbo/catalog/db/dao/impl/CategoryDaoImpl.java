/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.dao.impl;

import com.junbo.catalog.db.dao.CategoryDao;
import com.junbo.catalog.db.entity.CategoryEntity;

/**
 * Category DAO implementation.
 */
public class CategoryDaoImpl extends BaseDaoImpl<CategoryEntity> implements CategoryDao {
    @Override
    public CategoryEntity getCategory(Long categoryId, Long timestamp) {
        return get(categoryId, timestamp, "categoryId");
    }

   /* @Override
    public List<CategoryEntity> getCategories(final long categoryId, final int start, final int size) {
        return findAllBy(new Action<Criteria>() {
            public void apply(Criteria criteria) {
                criteria.setFirstResult(start);
                criteria.setFetchSize(size);
                criteria.add(Restrictions.eq("category_id", categoryId));
            }
        });
    }*/
}
