/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.id;

/**
 * Offer revision id.
 */
@IdResourcePath(value = "/offer-revisions/{0}",
                resourceType = "offer-revisions",
                regex = "/offer-revisions/(?<id>[0-9A-Za-z]+)")
public class OfferRevisionId extends CloudantId {
    public OfferRevisionId(){

    }

    public OfferRevisionId(String value) {
        super(value);
    }
}
