/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.dao;

import com.junbo.catalog.db.entity.ItemRevisionEntity;
import com.junbo.catalog.spec.model.item.ItemRevisionsGetOptions;

import java.util.List;

/**
 * Item DAO definition.
 */
public interface ItemRevisionDao extends BaseDao<ItemRevisionEntity> {
    List<ItemRevisionEntity> getRevisions(ItemRevisionsGetOptions options);
    ItemRevisionEntity getRevision(String itemId, Long timestamp);
    List<ItemRevisionEntity> getRevisions(String hostItemId);
}
