/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.iap;

import com.junbo.common.id.EntitlementId;

/**
 * The IAPEntitlementConsumeRequest class.
 */
public class IAPEntitlementConsumeRequest {

    private EntitlementId entitlement;

    private Integer useCountConsumed;

    private String trackingGuid;

    private String packageName;

    private String packageVersion;

    private String packageSignatureHash;

    public EntitlementId getEntitlement() {
        return entitlement;
    }

    public void setEntitlement(EntitlementId entitlement) {
        this.entitlement = entitlement;
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

    public String getPackageVersion() {
        return packageVersion;
    }

    public void setPackageVersion(String packageVersion) {
        this.packageVersion = packageVersion;
    }

    public String getPackageSignatureHash() {
        return packageSignatureHash;
    }

    public void setPackageSignatureHash(String packageSignatureHash) {
        this.packageSignatureHash = packageSignatureHash;
    }
}
