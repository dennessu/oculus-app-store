/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.store.spec.model.iap;

import com.junbo.common.cloudant.json.annotations.CloudantIgnore;
import com.junbo.common.id.EntitlementId;
import com.junbo.common.id.UserId;
import com.junbo.common.model.ResourceMeta;

/**
 * Entitlement Consumption for IAP.
 */
public class Consumption extends ResourceMeta<String> {

    private UserId userId;
    private EntitlementId entitlementId;
    private Integer useCountConsumed;
    private String sku;
    private String type;
    private String trackingGuid;
    private String packageName;
    @CloudantIgnore
    private Long signatureTimestamp;

    private String iapConsumptionData;
    private String iapSignature;

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

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public Long getSignatureTimestamp() {
        return signatureTimestamp;
    }

    public void setSignatureTimestamp(Long signatureTimestamp) {
        this.signatureTimestamp = signatureTimestamp;
    }

    @Override
    public String getId() {
        return getTrackingGuid();
    }

    @Override
    public void setId(String id) {
        setTrackingGuid(id);
    }

    public String getIapConsumptionData() {
        return iapConsumptionData;
    }

    public void setIapConsumptionData(String iapConsumptionData) {
        this.iapConsumptionData = iapConsumptionData;
    }

    public String getIapSignature() {
        return iapSignature;
    }

    public void setIapSignature(String iapSignature) {
        this.iapSignature = iapSignature;
    }
}
