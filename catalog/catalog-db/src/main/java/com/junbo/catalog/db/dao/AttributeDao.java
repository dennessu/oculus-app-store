/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.dao;

import com.junbo.catalog.db.entity.AttributeEntity;

import java.util.List;

/**
 * Attribute DAO definition.
 */
public interface AttributeDao extends BaseDao<AttributeEntity> {
    List<AttributeEntity> getAttributes(int start, int size);
}
