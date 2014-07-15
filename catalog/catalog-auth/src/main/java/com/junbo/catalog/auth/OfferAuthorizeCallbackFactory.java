/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.catalog.auth;

import com.junbo.authorization.AbstractAuthorizeCallbackFactory;
import com.junbo.authorization.AuthorizeCallback;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.catalog.spec.resource.OfferResource;
import org.springframework.beans.factory.annotation.Required;

/**
 * OfferAuthorizeCallbackFactory.
 */
public class OfferAuthorizeCallbackFactory extends AbstractAuthorizeCallbackFactory<Offer> {
    private OfferResource offerResource;

    @Required
    public void setOfferResource(OfferResource offerResource) {
        this.offerResource = offerResource;
    }

    @Override
    public AuthorizeCallback<Offer> create(Offer entity) {
        return new OfferAuthorizeCallback(this, entity);
    }

    public AuthorizeCallback<Offer> create(String offerId) {
        Offer offer = offerResource.getOffer(offerId).syncGet();
        return create(offer);
    }
}
