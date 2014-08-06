/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model;

import com.junbo.common.cloudant.json.annotations.CloudantIgnore;
import com.junbo.common.id.EntitlementId;
import com.junbo.common.id.ItemId;
import com.junbo.common.id.UserId;
import com.junbo.store.spec.model.purchase.AppDeliveryData;

/**
 * Entitlement used for IAP.
 */
public class Entitlement {
    private UserId userId;
    private ItemId itemId;
    private String itemType;
    private EntitlementId entitlementId;
    private AppDeliveryData appDeliveryData;
    private Integer useCount;
    private String type;
    private String sku;
    private Boolean isConsumable;
    private String packageName;
    private String iapEntitlementData;
    private String iapSignature;

    @CloudantIgnore
    private Long signatureTimestamp;

    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
    }

    public ItemId getItemId() {
        return itemId;
    }

    public void setItemId(ItemId itemId) {
        this.itemId = itemId;
    }

    public EntitlementId getEntitlementId() {
        return entitlementId;
    }

    public void setEntitlementId(EntitlementId entitlementId) {
        this.entitlementId = entitlementId;
    }

    public AppDeliveryData getAppDeliveryData() {
        return appDeliveryData;
    }

    public void setAppDeliveryData(AppDeliveryData appDeliveryData) {
        this.appDeliveryData = appDeliveryData;
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
