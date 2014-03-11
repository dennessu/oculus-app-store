/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.repo;

import com.junbo.catalog.db.convertor.CategoryConverter;
import com.junbo.catalog.db.dao.CategoryDao;
import com.junbo.catalog.spec.model.category.Category;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Category repository.
 */
public class CategoryRepository implements EntityRepository<Category> {
    @Autowired
    private CategoryDao categoryDao;

    @Override
    public Long create(Category category) {
        return categoryDao.create(CategoryConverter.toEntity(category));
    }

    @Override
    public Category get(Long id, Long timestamp) {
        return CategoryConverter.toModel(categoryDao.getCategory(id, timestamp));
    }
}
