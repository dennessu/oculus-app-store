/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.id;

/**
 * AttributeId.
 */
@IdResourcePath(value = "/offer-attributes/{0}",
                resourceType = "offer-attributes",
                regex = "/offer-attributes/(?<id>[0-9A-Za-z]+)")
public class OfferAttributeId extends CloudantId {

    public OfferAttributeId() {}
    public OfferAttributeId(String value) {
        super(value);
    }
}
