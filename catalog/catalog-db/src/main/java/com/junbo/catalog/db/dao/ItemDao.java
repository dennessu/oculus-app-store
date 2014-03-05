/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.dao;

import com.junbo.catalog.db.entity.ItemEntity;

/**
 * Item DAO definition.
 */
public interface ItemDao extends BaseDao<ItemEntity>  {
    ItemEntity getItem(long itemId, int revision);
    //List<ItemEntity> getItems(long itemId, int start, int size);
}
