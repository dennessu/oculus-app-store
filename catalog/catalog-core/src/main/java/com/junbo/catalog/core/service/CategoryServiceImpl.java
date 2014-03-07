/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.core.service;

import com.junbo.catalog.core.CategoryService;
import com.junbo.catalog.db.repo.CategoryDraftRepository;
import com.junbo.catalog.db.repo.CategoryRepository;
import com.junbo.catalog.spec.model.category.Category;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Category service implementation.
 */
public class CategoryServiceImpl extends BaseServiceImpl<Category> implements CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CategoryDraftRepository categoryDraftRepository;

    @Override
    public CategoryRepository getEntityRepo() {
        return categoryRepository;
    }

    @Override
    public CategoryDraftRepository getEntityDraftRepo() {
        return categoryDraftRepository;
    }
}
