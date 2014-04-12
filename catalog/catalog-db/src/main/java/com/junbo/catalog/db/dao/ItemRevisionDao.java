/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.dao;

import com.junbo.catalog.db.entity.ItemRevisionEntity;

/**
 * Item DAO definition.
 */
public interface ItemRevisionDao extends BaseDao<ItemRevisionEntity> {
    Long update(ItemRevisionEntity entity);
}
