/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.dao;

import com.junbo.catalog.common.util.EntityType;
import com.junbo.catalog.db.entity.ItemOfferRelationsEntity;

import java.util.List;

/**
 * Item DAO definition.
 */
public interface ItemOfferRelationsDao extends BaseDao<ItemOfferRelationsEntity> {
    List<ItemOfferRelationsEntity> getRelations(Long entityId, EntityType entityType);
    List<ItemOfferRelationsEntity> getRelations(Long entityId, EntityType entityType, Long parentOfferId);
}
