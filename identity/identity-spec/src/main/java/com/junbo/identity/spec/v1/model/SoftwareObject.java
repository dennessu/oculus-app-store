/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import com.junbo.common.jackson.annotation.XSSFreeString;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Created by liangfu on 8/7/2014.
 */
public class SoftwareObject {
    @XSSFreeString
    @ApiModelProperty(position = 1, required = false, value = "the version of the software object")
    private String version;

    @XSSFreeString
    @ApiModelProperty(position = 2, required = false, value = "the url to the software object")
    private String href;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }
}
