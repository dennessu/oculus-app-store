/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.catalog.spec.model.entitlementdef;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.jackson.annotation.EntitlementTypeId;

/**
 * entitlementType.
 */
public class EntitlementType {
    @EntitlementTypeId
    @JsonProperty("self")
    private String name;
    private Boolean isSubscription;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getIsSubscription() {
        return isSubscription;
    }

    public void setIsSubscription(Boolean isSubscription) {
        this.isSubscription = isSubscription;
    }
}
