/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.pricetier;

import com.junbo.catalog.spec.model.common.PageableGetOptions;

import javax.ws.rs.QueryParam;
import java.util.List;

/**
 * Price tiers get options.
 */
public class PriceTiersGetOptions extends PageableGetOptions {
    @QueryParam("tierId")
    private List<String> priceTierIds;
    @QueryParam("locale")
    private String locale;

    public List<String> getPriceTierIds() {
        return priceTierIds;
    }

    public void setPriceTierIds(List<String> priceTierIds) {
        this.priceTierIds = priceTierIds;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }
}
