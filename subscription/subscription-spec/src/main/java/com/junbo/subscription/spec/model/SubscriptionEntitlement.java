/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.subscription.spec.model;

import com.junbo.common.jackson.annotation.EntitlementId;
import com.junbo.common.jackson.annotation.SubscriptionId;

/**
 * Created by Administrator on 14-4-4.
 */
public class SubscriptionEntitlement {
    private Long id;

    @SubscriptionId
    private Long subscriptionId;

    @EntitlementId
    private String entitlementId;

    private Integer entitlementStatus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(Long subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public String getEntitlementId() {
        return entitlementId;
    }

    public void setEntitlementId(String entitlementId) {
        this.entitlementId = entitlementId;
    }

    public Integer getEntitlementStatus() {
        return entitlementStatus;
    }

    public void setEntitlementStatus(Integer entitlementStatus) {
        this.entitlementStatus = entitlementStatus;
    }


}
