/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 SilkCloud and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.catalog.data;

import com.junbo.catalog.spec.model.attribute.OfferAttribute;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.catalog.spec.model.offer.OfferRevision;
import com.junbo.identity.spec.v1.model.Organization;

import java.util.List;

/**
 * The OfferData class.
 */
public class OfferData {

    private Offer offer;

    private OfferRevision offerRevision;

    private List<OfferAttribute> categories;

    private Organization publisher;

    public Offer getOffer() {
        return offer;
    }

    public void setOffer(Offer offer) {
        this.offer = offer;
    }

    public OfferRevision getOfferRevision() {
        return offerRevision;
    }

    public void setOfferRevision(OfferRevision offerRevision) {
        this.offerRevision = offerRevision;
    }

    public List<OfferAttribute> getCategories() {
        return categories;
    }

    public void setCategories(List<OfferAttribute> categories) {
        this.categories = categories;
    }

    public Organization getPublisher() {
        return publisher;
    }

    public void setPublisher(Organization publisher) {
        this.publisher = publisher;
    }
}
