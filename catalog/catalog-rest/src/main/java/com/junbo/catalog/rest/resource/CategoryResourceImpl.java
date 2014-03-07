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

import javax.ws.rs.BeanParam;

/**
 * Category resource implementation.
 */
public class CategoryResourceImpl extends BaseResourceImpl<Category> implements CategoryResource {
    @Autowired
    private CategoryService categoryService;

    @Override
    protected CategoryService getEntityService() {
        return categoryService;
    }

    @Override
    public Promise<ResultList<Category>> getCategories(@BeanParam EntitiesGetOptions options) {
        return getEntities(options);
    }

    @Override
    public Promise<Category> getCategory(Long categoryId, @BeanParam EntityGetOptions options) {
        return get(categoryId, options);
    }
}
