/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.dao.impl;

import com.junbo.catalog.db.dao.OfferDao;
import com.junbo.catalog.db.entity.OfferEntity;

/**
 * Offer DAO implementation.
 */
public class OfferDaoImpl extends VersionedDaoImpl<OfferEntity> implements OfferDao {
    @Override
    public OfferEntity getOffer(Long offerId, Long timestamp) {
        return get(offerId, timestamp, "offerId");
    }
}
