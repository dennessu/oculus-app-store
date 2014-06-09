/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.catalog.auth;

import com.junbo.authorization.OwnerCallback;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.catalog.spec.resource.OfferResource;
import com.junbo.common.id.Id;
import com.junbo.common.id.OfferId;
import com.junbo.common.id.OrganizationId;
import com.junbo.common.id.UserId;
import org.springframework.beans.factory.annotation.Required;

/**
 * OfferOwnerCallback.
 */
public class OfferOwnerCallback implements OwnerCallback {
    private OfferResource offerResource;

    @Required
    public void setOfferResource(OfferResource offerResource) {
        this.offerResource = offerResource;
    }

    @Override
    public UserId getUserOwnerId(Id resourceId) {
        return null;
    }

    @Override
    public OrganizationId getOrganizationOwnerId(Id resourceId) {
        assert resourceId instanceof OfferId : "resourceId is not an OfferId";
        Offer offer = offerResource.getOffer((OfferId) resourceId).get();
        return offer.getOwnerId();
    }
}
