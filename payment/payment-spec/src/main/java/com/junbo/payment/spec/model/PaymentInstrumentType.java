/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.spec.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.jackson.annotation.PaymentInstrumentTypeId;
import com.junbo.common.jackson.annotation.XSSFreeString;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * payment instrument type model.
 */
public class PaymentInstrumentType {
    @ApiModelProperty(position = 1, required = true, value = "The id of payment instrument type resource.")
    @JsonProperty("self")
    @PaymentInstrumentTypeId
    private Long id;
    @ApiModelProperty(position = 2, required = true, value = "The type code of payment instrument resource.")
    @XSSFreeString
    private String typeCode;
    @ApiModelProperty(position = 3, required = true, value = "The description of payment instrument resource with different locales.")
    @XSSFreeString
    private String locales;
    @ApiModelProperty(position = 4, required = true, value = "whether the PI is recurring-able or not.")
    private Boolean capableOfRecurring;
    @ApiModelProperty(position = 5, required = true, value = "whether the PI is refund-able or not.")
    private Boolean isRefundable;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLocales() {
        return locales;
    }

    public void setLocales(String locales) {
        this.locales = locales;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public Boolean getCapableOfRecurring() {
        return capableOfRecurring;
    }

    public void setCapableOfRecurring(Boolean capableOfRecurring) {
        this.capableOfRecurring = capableOfRecurring;
    }

    public Boolean getIsRefundable() {
        return isRefundable;
    }

    public void setIsRefundable(Boolean isRefundable) {
        this.isRefundable = isRefundable;
    }
}
