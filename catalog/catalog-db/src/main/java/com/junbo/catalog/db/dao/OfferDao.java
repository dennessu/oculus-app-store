/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.dao;

import com.junbo.catalog.db.entity.OfferEntity;

/**
 * Offer DAO definition.
 */
public interface OfferDao extends BaseDao<OfferEntity> {
    Long update(OfferEntity offerEntity);
}
