/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model;

import com.junbo.common.cloudant.json.annotations.CloudantIgnore;

/**
 * Entitlement used for IAP.
 */
public class Entitlement {
    private String userId;
    private String username;
    private String itemId;
    private String itemType;
    private String entitlementId;
    private Integer useCount;
    private String type;
    private String sku;
    private Boolean isConsumable;
    private String packageName;
    private String iapEntitlementData;
    private String iapSignature;

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

    public void setSignatureTimestamp(Long signatureTimestamp) {
        this.signatureTimestamp = signatureTimestamp;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getIapEntitlementData() {
        return iapEntitlementData;
    }

    public void setIapEntitlementData(String iapEntitlementData) {
        this.iapEntitlementData = iapEntitlementData;
    }

    public String getIapSignature() {
        return iapSignature;
    }

    public void setIapSignature(String iapSignature) {
        this.iapSignature = iapSignature;
    }

}
