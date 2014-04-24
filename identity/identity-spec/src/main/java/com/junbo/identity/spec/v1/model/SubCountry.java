/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Created by liangfu on 4/24/14.
 */
public class SubCountry {

    @ApiModelProperty(position = 1, required = true, value = "Short name key of the sub country.")
    private String shortNameKey;

    @ApiModelProperty(position = 2, required = true, value = "Long name key of the sub country.")
    private String longNameKey;

    public String getShortNameKey() {
        return shortNameKey;
    }

    public void setShortNameKey(String shortNameKey) {
        this.shortNameKey = shortNameKey;
    }

    public String getLongNameKey() {
        return longNameKey;
    }

    public void setLongNameKey(String longNameKey) {
        this.longNameKey = longNameKey;
    }
}
