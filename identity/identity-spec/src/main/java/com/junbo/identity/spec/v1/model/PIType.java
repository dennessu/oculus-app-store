/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.junbo.common.enumid.PITypeId;
import com.junbo.common.model.ResourceMeta;
import com.junbo.common.util.Identifiable;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by haomin on 14-4-25.
 */
public class PIType extends ResourceMeta implements Identifiable<PITypeId> {
    @ApiModelProperty(position = 1, required = true, value = "The id of payment instrument type resource.")
    @JsonProperty("self")
    private PITypeId id;

    @ApiModelProperty(position = 2, required = true, value = "The type code of payment instrument resource.")
    private String typeCode;

    @ApiModelProperty(position = 3, required = true, value = "The description of payment instrument resource.")
    private Map<String, JsonNode> locales = new HashMap<>();

    @ApiModelProperty(position = 4, required = true, value = "whether the PI is recurring-able or not.")
    private Boolean capableOfRecurring;

    @ApiModelProperty(position = 5, required = true, value = "whether the PI is refund-able or not.")
    private Boolean isRefundable;

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public Map<String, JsonNode> getLocales() {
        return locales;
    }

    public void setLocales(Map<String, JsonNode> locales) {
        this.locales = locales;
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

    public PITypeId getId() {
        return id;
    }

    public void setId(PITypeId id) {
        this.id = id;
    }
}
