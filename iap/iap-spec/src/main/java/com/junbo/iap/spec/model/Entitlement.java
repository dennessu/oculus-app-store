/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.iap.spec.model;

import com.junbo.common.cloudant.json.annotations.CloudantIgnore;

/**
 * Entitlement used for IAP.
 */
public class Entitlement implements SignedModel {
    private String userId;
    private String entitlementId;
    private Integer useCount;
    private String sku;
    private String type;
    private Boolean isConsumable;
    private String packageName;

    @CloudantIgnore
    private Long signatureTimestamp;

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

    public Integer getUseCount() {
        return useCount;
    }

    public void setUseCount(Integer useCount) {
        this.useCount = useCount;
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

    public Boolean getIsConsumable() {
        return isConsumable;
    }

    public void setIsConsumable(Boolean isConsumable) {
        this.isConsumable = isConsumable;
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

    @Override
    public void setSignatureTimestamp(Long signatureTimestamp) {
        this.signatureTimestamp = signatureTimestamp;
    }
}
