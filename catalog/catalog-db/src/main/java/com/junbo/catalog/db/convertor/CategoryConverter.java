/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.convertor;

import com.junbo.catalog.common.util.Utils;
import com.junbo.catalog.db.entity.CategoryDraftEntity;
import com.junbo.catalog.db.entity.CategoryEntity;
import com.junbo.catalog.spec.model.category.Category;

/**
 * Category converter between model and DB entity.
 */
public class CategoryConverter {
    private CategoryConverter(){}

    public static CategoryDraftEntity toDraftEntity(Category model) {
        CategoryDraftEntity entity = new CategoryDraftEntity();
        fillDraftEntity(model, entity);
        return entity;
    }

    public static void fillDraftEntity(Category model, CategoryDraftEntity entity) {
        entity.setName(model.getName());
        entity.setStatus(model.getStatus());
        entity.setRevision(model.getRevision());
        entity.setParentId(model.getParentId());
        entity.setPayload(Utils.toJson(model));
    }

    public static Category toModel(CategoryDraftEntity entity) {
        Category model = Utils.fromJson(entity.getPayload(), Category.class);
        model.setId(entity.getId());
        model.setName(entity.getName());
        model.setRevision(entity.getRevision());
        model.setStatus(entity.getStatus());
        model.setParentId(entity.getParentId());
        model.setCreatedBy(entity.getCreatedBy());
        model.setCreatedTime(entity.getCreatedTime());
        model.setUpdatedBy(entity.getUpdatedBy());
        model.setUpdatedTime(entity.getUpdatedTime());
        return model;
    }

    public static CategoryEntity toEntity(Category model) {
        CategoryEntity entity = new CategoryEntity();
        entity.setCategoryId(model.getId());
        entity.setName(model.getName());
        entity.setStatus(model.getStatus());
        entity.setRevision(model.getRevision());
        entity.setParentId(model.getParentId());
        entity.setPayload(Utils.toJson(model));
        return entity;
    }


    public static Category toModel(CategoryEntity entity) {
        Category model = Utils.fromJson(entity.getPayload(), Category.class);
        model.setId(entity.getCategoryId());
        model.setName(entity.getName());
        model.setStatus(entity.getStatus());
        model.setParentId(entity.getParentId());
        model.setCreatedBy(entity.getCreatedBy());
        model.setCreatedTime(entity.getCreatedTime());
        model.setUpdatedBy(entity.getUpdatedBy());
        model.setUpdatedTime(entity.getUpdatedTime());
        return model;
    }
}
