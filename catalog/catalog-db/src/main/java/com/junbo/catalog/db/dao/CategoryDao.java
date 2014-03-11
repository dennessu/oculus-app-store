/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.dao;

import com.junbo.catalog.db.entity.CategoryEntity;

/**
 * Category DAO definition.
 */
public interface CategoryDao extends BaseDao<CategoryEntity>  {
    CategoryEntity getCategory(Long categoryId, Long timestamp);
    //List<CategoryEntity> getCategories(long categoryId, int start, int size);
}
