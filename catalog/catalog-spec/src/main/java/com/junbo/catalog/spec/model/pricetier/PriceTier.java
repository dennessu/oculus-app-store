/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.pricetier;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.catalog.spec.model.common.BaseModel;
import com.junbo.catalog.spec.model.offer.Price;
import com.junbo.common.jackson.annotation.AttributeId;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * PriceTier.
 */
public class PriceTier extends BaseModel {
    @AttributeId
    @JsonProperty("self")
    private Long id;
    @NotNull
    private String name;
    @NotNull
    private Map<String, Price> prices;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Price> getPrices() {
        return prices;
    }

    public void setPrices(Map<String, Price> prices) {
        this.prices = prices;
    }
}
