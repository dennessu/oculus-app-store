/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.id;

/**
 * PriceTierId.
 */
@IdResourcePath(value = "/price-tiers/{0}", regex = "/price-tiers/(?<id>[0-9A-Z]+)")
public class PriceTierId extends Id {

    public PriceTierId() {}
    public PriceTierId(long value) {
        super(value);
    }
}
