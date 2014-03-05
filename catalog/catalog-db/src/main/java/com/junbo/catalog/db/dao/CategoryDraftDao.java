/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.dao;

import com.junbo.catalog.db.entity.CategoryDraftEntity;

import java.util.List;

/**
 * Category draft DAO definition.
 */
public interface CategoryDraftDao extends BaseDao<CategoryDraftEntity> {
    List<CategoryDraftEntity> getCategories(int start, int size);
}
