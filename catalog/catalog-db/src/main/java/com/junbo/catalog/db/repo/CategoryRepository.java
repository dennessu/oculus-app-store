/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.repo;

import com.junbo.catalog.db.convertor.CategoryConverter;
import com.junbo.catalog.db.dao.CategoryDao;
import com.junbo.catalog.db.entity.CategoryEntity;
import com.junbo.catalog.spec.model.category.Category;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Category repository.
 */
public class CategoryRepository {
    @Autowired
    private CategoryDao categoryDao;

    public Long create(Category category) {
        CategoryEntity entity = CategoryConverter.toEntity(category);
        return categoryDao.create(entity);
    }

    public Category get(Long id, Integer revision) {
        CategoryEntity entity = categoryDao.getCategory(id, revision);
        Category category = CategoryConverter.toModel(entity);
        return category;
    }
}
