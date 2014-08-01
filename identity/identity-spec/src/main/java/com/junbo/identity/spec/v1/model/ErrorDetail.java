/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import com.junbo.common.jackson.annotation.XSSFreeString;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Created by liangfu on 7/22/14.
 */
public class ErrorDetail {
    @XSSFreeString
    @ApiModelProperty(position = 1, required = false, value = "Error title.")
    private String errorTitle;

    @XSSFreeString
    @ApiModelProperty(position = 2, required = false, value = "Error summary.")
    private String errorSummary;

    @XSSFreeString
    @ApiModelProperty(position = 3, required = false, value = "Error information.")
    private String errorInformation;

    @XSSFreeString
    @ApiModelProperty(position = 4, required = false, value = "Error support link.")
    private String supportLink;

    public String getErrorTitle() {
        return errorTitle;
    }

    public void setErrorTitle(String errorTitle) {
        this.errorTitle = errorTitle;
    }

    public String getErrorSummary() {
        return errorSummary;
    }

    public void setErrorSummary(String errorSummary) {
        this.errorSummary = errorSummary;
    }

    public String getErrorInformation() {
        return errorInformation;
    }

    public void setErrorInformation(String errorInformation) {
        this.errorInformation = errorInformation;
    }

    public String getSupportLink() {
        return supportLink;
    }

    public void setSupportLink(String supportLink) {
        this.supportLink = supportLink;
    }
}
