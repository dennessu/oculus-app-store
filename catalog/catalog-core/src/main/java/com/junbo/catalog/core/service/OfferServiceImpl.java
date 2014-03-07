/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.core.service;

import com.junbo.catalog.core.OfferService;
import com.junbo.catalog.db.repo.OfferDraftRepository;
import com.junbo.catalog.db.repo.OfferRepository;
import com.junbo.catalog.spec.model.offer.Offer;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Offer service implementation.
 */
public class OfferServiceImpl extends BaseServiceImpl<Offer> implements OfferService {
    @Autowired
    private OfferRepository offerRepository;
    @Autowired
    private OfferDraftRepository offerDraftRepository;

    @Override
    public OfferRepository getEntityRepo() {
        return offerRepository;
    }

    @Override
    public OfferDraftRepository getEntityDraftRepo() {
        return offerDraftRepository;
    }
}
