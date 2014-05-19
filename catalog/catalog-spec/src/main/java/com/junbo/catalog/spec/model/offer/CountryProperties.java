/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.offer;

import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * Country properties for offer revision.
 */
public class CountryProperties {
    @ApiModelProperty(position = 1, required = true, value = "Whether the offer can be purchased from this country")
    private Boolean isPurchasable;
    @ApiModelProperty(position = 2, required = true, value = "Pre-order release date")
    private Date releaseDate;

    public Boolean getIsPurchasable() {
        return isPurchasable;
    }

    public void setIsPurchasable(Boolean isPurchasable) {
        this.isPurchasable = isPurchasable;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }
}
