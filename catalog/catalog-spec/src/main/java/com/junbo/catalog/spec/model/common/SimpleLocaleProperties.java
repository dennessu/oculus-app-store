/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.common;

import com.junbo.common.jackson.annotation.XSSFreeRichText;
import com.junbo.common.jackson.annotation.XSSFreeString;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Attribute locales.
 */
public class SimpleLocaleProperties {
    @XSSFreeString
    @ApiModelProperty(position = 1, required = true, value = "attribute name")
    String name;

    @XSSFreeRichText
    @ApiModelProperty(position = 2, required = true, value = "description")
    String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
