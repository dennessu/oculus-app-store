/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.catalog.auth;

import com.junbo.authorization.AbstractAuthorizeCallback;
import com.junbo.catalog.spec.model.offer.Offer;

/**
 * OfferAuthorizeCallback.
 */
public class OfferAuthorizeCallback extends AbstractAuthorizeCallback<Offer> {
    OfferAuthorizeCallback(OfferAuthorizeCallbackFactory factory, Offer entity) {
        super(factory, entity);
    }

    @Override
    public String getApiName() {
        return "offers";
    }

    @Override
    protected Object getEntityIdByPropertyPath(String propertyPath) {
        if ("owner".equals(propertyPath)) {
            return getEntity().getOwnerId();
        }

        return super.getEntityIdByPropertyPath(propertyPath);
    }
}
