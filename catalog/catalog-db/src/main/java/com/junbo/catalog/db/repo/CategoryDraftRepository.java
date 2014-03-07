/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.repo;

import com.junbo.catalog.db.convertor.CategoryConverter;
import com.junbo.catalog.db.dao.CategoryDraftDao;
import com.junbo.catalog.db.entity.CategoryDraftEntity;
import com.junbo.catalog.spec.model.category.Category;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * Category draft repository.
 */
public class CategoryDraftRepository implements EntityDraftRepository<Category> {
    @Autowired
    private CategoryDraftDao categoryDraftDao;

    @Override
    public Long create(Category category) {
        CategoryDraftEntity entity = CategoryConverter.toDraftEntity(category);
        return categoryDraftDao.create(entity);
    }

    @Override
    public Category get(Long id) {
        CategoryDraftEntity entity = categoryDraftDao.get(id);
        return CategoryConverter.toModel(entity);
    }

    @Override
    public Long update(Category category) {
        // TODO
        return null;
    }

    @Override
    public List<Category> getEntities(int start, int size) {
        return getCategories(start, size);
    }

    public List<Category> getCategories(int start, int size) {
        List<CategoryDraftEntity> entities = categoryDraftDao.getCategories(start, size);
        List<Category> result = new ArrayList<>();
        for (CategoryDraftEntity entity : entities) {
            result.add(CategoryConverter.toModel(entity));
        }

        return result;
    }
}
