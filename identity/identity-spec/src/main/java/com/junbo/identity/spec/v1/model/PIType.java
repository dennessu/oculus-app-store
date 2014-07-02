/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.junbo.common.id.PITypeId;
import com.junbo.common.model.PropertyAssignedAwareResourceMeta;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by haomin on 14-4-25.
 */
public class PIType extends PropertyAssignedAwareResourceMeta<PITypeId> {
    @ApiModelProperty(position = 1, required = true, value = "[Client Immutable] Link to payment instrument type resource.")
    @JsonProperty("self")
    private PITypeId id;

    @ApiModelProperty(position = 2, required = true, value = "The type code of payment instrument resource, ENUM value include " +
            "(CREDITCARD, DIRECTDEBIT, STOREDVALUE, PAYPAL, OTHERS).")
    private String typeCode;

    @ApiModelProperty(position = 3, required = true, value = "True if this PI is recurring-abled; False if this PI is not recurring-abled.")
    private Boolean capableOfRecurring;

    @ApiModelProperty(position = 4, required = true, value = "True if this PI is refund-able, False if this PI is not refund-able.")
    private Boolean isRefundable;

    @ApiModelProperty(position = 5, required = true, value = "The description of payment instrument resource.")
    private Map<String, JsonNode> locales = new HashMap<>();

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
        support.setPropertyAssigned("typeCode");
    }

    public Map<String, JsonNode> getLocales() {
        return locales;
    }

    public void setLocales(Map<String, JsonNode> locales) {
        this.locales = locales;
        support.setPropertyAssigned("locales");
    }

    public Boolean getCapableOfRecurring() {
        return capableOfRecurring;
    }

    public void setCapableOfRecurring(Boolean capableOfRecurring) {
        this.capableOfRecurring = capableOfRecurring;
        support.setPropertyAssigned("capableOfRecurring");
    }

    public Boolean getIsRefundable() {
        return isRefundable;
    }

    public void setIsRefundable(Boolean isRefundable) {
        this.isRefundable = isRefundable;
        support.setPropertyAssigned("isRefundable");
    }

    public PITypeId getId() {
        return id;
    }

    public void setId(PITypeId id) {
        this.id = id;
        support.setPropertyAssigned("id");
        support.setPropertyAssigned("self");
    }
}
