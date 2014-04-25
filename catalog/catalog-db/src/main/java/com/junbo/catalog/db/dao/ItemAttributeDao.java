/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.dao;

import com.junbo.catalog.db.entity.ItemAttributeEntity;
import com.junbo.catalog.spec.model.item.ItemAttributesGetOptions;

import java.util.List;

/**
 * Item Attribute DAO definition.
 */
public interface ItemAttributeDao extends BaseDao<ItemAttributeEntity> {
    List<ItemAttributeEntity> getAttributes(ItemAttributesGetOptions options);
}
