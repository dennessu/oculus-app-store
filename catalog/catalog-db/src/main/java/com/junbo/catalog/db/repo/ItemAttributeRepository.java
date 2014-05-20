/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.repo;

import com.junbo.catalog.spec.model.attribute.ItemAttribute;
import com.junbo.catalog.spec.model.attribute.ItemAttributesGetOptions;

import java.util.List;

/**
 * Item repository.
 */
public interface ItemAttributeRepository extends AttributeRepository<ItemAttribute> {
    Long create(ItemAttribute attribute);
    ItemAttribute get(Long attributeId);
    List<ItemAttribute> getAttributes(ItemAttributesGetOptions options);
    Long update(ItemAttribute attribute);
    void delete(Long attributeId);
}
