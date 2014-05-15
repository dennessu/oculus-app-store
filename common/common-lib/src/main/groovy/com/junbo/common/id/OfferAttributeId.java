/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.id;

/**
 * AttributeId.
 */
@IdResourcePath(value = "/offer-attributes/{0}", regex = "/offer-attributes/(?<id>[0-9A-Z]+)")
public class OfferAttributeId extends Id {

    public OfferAttributeId() {}
    public OfferAttributeId(long value) {
        super(value);
    }
}
