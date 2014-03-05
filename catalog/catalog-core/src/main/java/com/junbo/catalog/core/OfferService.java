/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.core;

import com.junbo.catalog.spec.model.common.EntityGetOptions;
import com.junbo.catalog.spec.model.offer.Offer;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Offer service definition.
 */
@Transactional
public interface OfferService {
    Offer getOffer(Long offerId, EntityGetOptions options);
    //Offer getDraftOffer(Long offerId);
    List<Offer> getOffers(int start, int size);
    Offer createOffer(Offer offer);
    Offer updateOffer(Offer offer);
    Offer reviewOffer(Long offerId);
    Offer publishOffer(Long offerId);
    Long removeOffer(Long offerId);
    Long deleteOffer(Long offerId);
}
