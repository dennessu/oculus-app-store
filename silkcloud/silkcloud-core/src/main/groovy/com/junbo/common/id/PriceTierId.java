/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.id;

/**
 * PriceTierId.
 */
@IdResourcePath(value = "/price-tiers/{0}",
                resourceType = "price-tiers",
                regex = "/price-tiers/(?<id>[0-9A-Za-z]+)")
public class PriceTierId extends CloudantId {

    public PriceTierId() {}
    public PriceTierId(String value) {
        super(value);
    }
}
