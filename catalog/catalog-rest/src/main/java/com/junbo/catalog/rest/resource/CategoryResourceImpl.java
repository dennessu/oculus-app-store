/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.rest.resource;

import com.junbo.catalog.core.CategoryService;
import com.junbo.catalog.spec.model.category.Category;
import com.junbo.catalog.spec.model.common.EntitiesGetOptions;
import com.junbo.catalog.spec.model.common.EntityGetOptions;
import com.junbo.catalog.spec.model.common.ResultList;
import com.junbo.catalog.spec.resource.CategoryResource;
import com.junbo.langur.core.promise.Promise;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Category resource implementation.
 */
public class CategoryResourceImpl implements CategoryResource {
    @Autowired
    private CategoryService categoryService;

    @Override
    public Promise<ResultList<Category>> getCategories(EntitiesGetOptions options) {
        List<Category> categories = categoryService.getEntities(options);
        ResultList<Category> resultList = new ResultList<>();
        resultList.setResults(categories);
        resultList.setHref("href TODO");
        resultList.setNext("next TODO");
        return Promise.pure(resultList);
    }

    @Override
    public Promise<Category> getCategory(Long categoryId, EntityGetOptions options) {
        return Promise.pure(categoryService.get(categoryId, options));
    }

    @Override
    public Promise<Category> createCategory(Category category) {
        return Promise.pure(categoryService.create(category));
    }
}
