/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.core;

import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.catalog.spec.model.offer.OfferRevision;
import com.junbo.catalog.spec.model.offer.OffersGetOptions;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Offer service definition.
 */
@Transactional
public interface OfferService extends BaseRevisionedService<Offer, OfferRevision> {
    List<Offer> getOffers(OffersGetOptions options);
}
