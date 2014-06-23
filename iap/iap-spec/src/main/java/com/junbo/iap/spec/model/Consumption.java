/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.iap.spec.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.junbo.common.cloudant.json.annotations.CloudantIgnore;
import com.junbo.common.model.ResourceMeta;
import com.junbo.common.model.SigningSupport;

/**
 * Entitlement Consumption for IAP.
 */
public class Consumption extends ResourceMeta<String> implements SigningSupport {

    private String userId;
    private String entitlementId;
    private Integer useCountConsumed;
    private String sku;
    private String type;
    private String trackingGuid;
    private String packageName;
    @CloudantIgnore
    private Long signatureTimestamp;
    @JsonIgnore
    private String signingKey;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEntitlementId() {
        return entitlementId;
    }

    public void setEntitlementId(String entitlementId) {
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

    @Override
    public String getSigningKey() {
        return signingKey;
    }

    public void setSigningKey(String signingKey) {
        this.signingKey = signingKey;
    }
}
