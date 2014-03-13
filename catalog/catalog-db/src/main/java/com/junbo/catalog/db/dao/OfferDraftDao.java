/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.dao;

import com.junbo.catalog.db.entity.OfferDraftEntity;

import java.util.List;

/**
 * Offer draft DAO definition.
 */
public interface OfferDraftDao extends VersionedDao<OfferDraftEntity> {
    List<OfferDraftEntity> getOffers(int start, int size);
}
