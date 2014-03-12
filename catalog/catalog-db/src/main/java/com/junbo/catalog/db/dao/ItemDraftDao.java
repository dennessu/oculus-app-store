/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.dao;

import com.junbo.catalog.db.entity.ItemDraftEntity;

import java.util.List;

/**
 * Item draft DAO definition.
 */
public interface ItemDraftDao extends VersionedDao<ItemDraftEntity> {
    List<ItemDraftEntity> getItems(int start, int size);
}
