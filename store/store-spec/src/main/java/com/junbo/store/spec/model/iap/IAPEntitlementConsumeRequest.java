/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.iap;

import com.junbo.common.id.EntitlementId;
import com.junbo.common.id.UserId;

/**
 * The IAPEntitlementConsumeRequest class.
 */
public class IAPEntitlementConsumeRequest {

    private UserId userId;
    private EntitlementId entitlementId;
    private Integer useCountConsumed;
    private String trackingGuid;
    private String packageName;


    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
    }

    public EntitlementId getEntitlementId() {
        return entitlementId;
    }

    public void setEntitlementId(EntitlementId entitlementId) {
        this.entitlementId = entitlementId;
    }

    public Integer getUseCountConsumed() {
        return useCountConsumed;
    }

    public void setUseCountConsumed(Integer useCountConsumed) {
        this.useCountConsumed = useCountConsumed;
    }

    public String getTrackingGuid() {
        return trackingGuid;
    }

    public void setTrackingGuid(String trackingGuid) {
        this.trackingGuid = trackingGuid;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
