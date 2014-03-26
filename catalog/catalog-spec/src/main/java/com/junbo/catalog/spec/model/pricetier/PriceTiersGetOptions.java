/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.pricetier;

import com.junbo.catalog.spec.model.common.PageableGetOptions;
import com.junbo.common.id.PriceTierId;

import javax.ws.rs.QueryParam;
import java.util.List;

/**
 * Price tiers get options.
 */
public class PriceTiersGetOptions extends PageableGetOptions {
    @QueryParam("id")
    private List<PriceTierId> priceTierIds;

    public List<PriceTierId> getPriceTierIds() {
        return priceTierIds;
    }

    public void setPriceTierIds(List<PriceTierId> priceTierIds) {
        this.priceTierIds = priceTierIds;
    }
}
