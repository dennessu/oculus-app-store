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

    private UserId user;
    private EntitlementId entitlement;
    private Integer useCountConsumed;
    private String sku;
    private String trackingGuid;
    private String packageName;
    @CloudantIgnore
    private Long signatureTimestamp;

    private String payload;
    private String signature;

    public UserId getUser() {
        return user;
    }

    public void setUser(UserId user) {
        this.user = user;
    }

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

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
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

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
