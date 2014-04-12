/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.dao;

import com.junbo.catalog.db.entity.OfferRevisionEntity;

/**
 * Offer DAO definition.
 */
public interface OfferRevisionDao extends BaseDao<OfferRevisionEntity> {
    Long update(OfferRevisionEntity entity);
}
