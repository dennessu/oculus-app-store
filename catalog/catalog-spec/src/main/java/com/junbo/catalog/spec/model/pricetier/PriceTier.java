/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.pricetier;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.catalog.spec.model.common.BaseModel;
import com.junbo.catalog.spec.model.common.SimpleLocaleProperties;
import com.junbo.common.jackson.annotation.PriceTierId;
import com.wordnik.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Map;

/**
 * PriceTier.
 */
public class PriceTier extends BaseModel {
    @PriceTierId
    @JsonProperty("self")
    @ApiModelProperty(position = 1, required = true, value = "Price tier id")
    private String id;
    @NotNull
    @ApiModelProperty(position = 2, required = true, value = "Prices")
    private Map<String, Map<String, BigDecimal>> prices;
    @NotNull
    @ApiModelProperty(position = 3, required = true, value = "Locale properties")
    private Map<String, SimpleLocaleProperties> locales;
    @ApiModelProperty(position = 4, required = true,
            value = "This is the calculated value to give how accurate the localizable attributes is.",
            allowableValues = "HIGH, MEDIUM, LOW")
    private String localeAccuracy;

    @JsonIgnore
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, Map<String, BigDecimal>> getPrices() {
        return prices;
    }

    public void setPrices(Map<String, Map<String, BigDecimal>> prices) {
        this.prices = prices;
    }

    public Map<String, SimpleLocaleProperties> getLocales() {
        return locales;
    }

    public void setLocales(Map<String, SimpleLocaleProperties> locales) {
        this.locales = locales;
    }

    public String getLocaleAccuracy() {
        return localeAccuracy;
    }

    public void setLocaleAccuracy(String localeAccuracy) {
        this.localeAccuracy = localeAccuracy;
    }
}
