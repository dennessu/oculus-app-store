/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.core;

import com.junbo.catalog.spec.model.category.Category;
import com.junbo.catalog.spec.model.common.EntityGetOptions;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Category service definition.
 */
@Transactional
public interface CategoryService {
    Category getCategory(Long id, EntityGetOptions options);
    List<Category> getCategories(int start, int size);
    Category createCategory(Category category);
}
