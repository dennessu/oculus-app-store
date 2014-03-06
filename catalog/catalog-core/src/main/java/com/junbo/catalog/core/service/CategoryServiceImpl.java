/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.core.service;

import com.junbo.catalog.common.util.Constants;
import com.junbo.catalog.core.CategoryService;
import com.junbo.catalog.db.repo.CategoryDraftRepository;
import com.junbo.catalog.db.repo.CategoryRepository;
import com.junbo.catalog.spec.model.category.Category;
import com.junbo.catalog.spec.model.common.EntityGetOptions;
import com.junbo.catalog.spec.model.common.Status;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Category service implementation.
 */
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryDraftRepository categoryDraftRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Override
    public Category getCategory(Long categoryId, EntityGetOptions options) {
        return categoryDraftRepository.get(categoryId);
    }

    @Override
    public List<Category> getCategories(int start, int size) {
        return categoryDraftRepository.getCategories(start, size);
    }

    @Override
    public Category createCategory(Category category) {
        // TODO: validations

        category.setRevision(Constants.INITIAL_CREATION_REVISION);
        category.setStatus(Status.DESIGN);

        Long categoryId = categoryDraftRepository.create(category);
        category.setId(categoryId);
        categoryRepository.create(category);

        return categoryDraftRepository.get(category.getId());
    }
}
