/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import com.junbo.common.jackson.annotation.XSSFreeString;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Created by liangfu on 6/12/14.
 */
public class CurrencyLocaleKey {

    @XSSFreeString
    @ApiModelProperty(position = 1, required = true, value = "Short name.")
    private String shortName;

    @XSSFreeString
    @ApiModelProperty(position = 2, required = true, value = "Long name.")
    private String longName;

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }
}
