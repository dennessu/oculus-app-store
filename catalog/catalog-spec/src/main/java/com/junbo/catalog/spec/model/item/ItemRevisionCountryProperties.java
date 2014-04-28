/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.item;

import com.junbo.common.jackson.annotation.AgeRatingId;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Country properties for item revision.
 */
public class ItemRevisionCountryProperties {
    @ApiModelProperty(position = 28, required = false, value = "Support phone")
    private String supportPhone;
    @ApiModelProperty(position = 36, required = true,
            value = "The content rating given to the item by a specific board (ESRB, PEGI)")
    @AgeRatingId
    private String ageRating;

    public String getSupportPhone() {
        return supportPhone;
    }

    public void setSupportPhone(String supportPhone) {
        this.supportPhone = supportPhone;
    }

    public String getAgeRating() {
        return ageRating;
    }

    public void setAgeRating(String ageRating) {
        this.ageRating = ageRating;
    }
}
