/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.pricetier;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.catalog.spec.model.common.SimpleLocaleProperties;
import com.junbo.catalog.spec.model.common.BaseModel;
import com.junbo.common.jackson.annotation.AttributeId;
import com.wordnik.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Map;

/**
 * PriceTier.
 */
public class PriceTier extends BaseModel {
    @AttributeId
    @JsonProperty("self")
    @ApiModelProperty(position = 1, required = true, value = "Price tier id")
    private Long id;
    @NotNull
    @ApiModelProperty(position = 2, required = true, value = "Prices")
    private Map<String, BigDecimal> prices;
    @NotNull
    @ApiModelProperty(position = 3, required = true, value = "Locale properties")
    private Map<String, SimpleLocaleProperties> locales;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Map<String, BigDecimal> getPrices() {
        return prices;
    }

    public void setPrices(Map<String, BigDecimal> prices) {
        this.prices = prices;
    }

    public Map<String, SimpleLocaleProperties> getLocales() {
        return locales;
    }

    public void setLocales(Map<String, SimpleLocaleProperties> locales) {
        this.locales = locales;
    }
}
