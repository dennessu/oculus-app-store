/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.item;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Entitlement Definition.
 */
public class EntitlementDef {
    @ApiModelProperty(position = 1, required = true, value = "The entitlement type",
            allowableValues = "DOWNLOAD, RUN, DEVELOPER, ALLOW_IN_APP")
    private String type;
    @JsonProperty("isConsumable")
    @ApiModelProperty(position = 1, required = true, value = "True if this is an consumable")
    private Boolean consumable;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getConsumable() {
        return consumable;
    }

    public void setConsumable(Boolean consumable) {
        this.consumable = consumable;
    }
}
