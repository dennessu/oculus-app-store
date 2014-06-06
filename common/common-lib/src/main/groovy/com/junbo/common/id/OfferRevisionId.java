/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.id;

/**
 * Offer revision id.
 */
@IdResourcePath(value = "/offers-revisions/{0}", regex = "/offers-revisions/(?<id>[0-9A-Z]+)")
public class OfferRevisionId extends Id {
    public OfferRevisionId(){

    }

    public OfferRevisionId(Long value) {
        super(value);
    }
}
