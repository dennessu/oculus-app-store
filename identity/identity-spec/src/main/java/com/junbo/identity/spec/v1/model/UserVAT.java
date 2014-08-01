/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import com.junbo.common.jackson.annotation.XSSFreeString;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * Created by liangfu on 6/16/14.
 */
public class UserVAT {
    @XSSFreeString
    @ApiModelProperty(position = 1, required = true, value = "vatNumber")
    private String vatNumber;

    @ApiModelProperty(position = 2, required = false, value = "LastValidateTime of vatNumber.")
    private Date lastValidateTime;

    public String getVatNumber() {
        return vatNumber;
    }

    public void setVatNumber(String vatNumber) {
        this.vatNumber = vatNumber;
    }

    public Date getLastValidateTime() {
        return lastValidateTime;
    }

    public void setLastValidateTime(Date lastValidateTime) {
        this.lastValidateTime = lastValidateTime;
    }
}
