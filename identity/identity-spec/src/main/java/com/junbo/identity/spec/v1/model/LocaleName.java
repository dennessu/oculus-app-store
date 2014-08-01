/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import com.junbo.common.jackson.annotation.XSSFreeRichText;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Created by liangfu on 4/24/14.
 */
public class LocaleName {
    @XSSFreeRichText
    @ApiModelProperty(position = 1, required = true, value = "The description of LocaleName.")
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
