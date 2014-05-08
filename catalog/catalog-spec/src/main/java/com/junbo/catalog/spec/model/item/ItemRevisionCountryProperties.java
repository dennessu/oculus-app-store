/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.item;

import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Country properties for item revision.
 */
public class ItemRevisionCountryProperties {
    @ApiModelProperty(position = 28, required = false, value = "Support phone")
    private String supportPhone;

    public String getSupportPhone() {
        return supportPhone;
    }

    public void setSupportPhone(String supportPhone) {
        this.supportPhone = supportPhone;
    }
}
