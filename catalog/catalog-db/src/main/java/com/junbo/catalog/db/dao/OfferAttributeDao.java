/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.dao;

import com.junbo.catalog.db.entity.OfferAttributeEntity;
import com.junbo.catalog.spec.model.offer.OfferAttributesGetOptions;

import java.util.List;

/**
 * Item Attribute DAO definition.
 */
public interface OfferAttributeDao extends BaseDao<OfferAttributeEntity> {
    List<OfferAttributeEntity> getAttributes(OfferAttributesGetOptions options);
}
