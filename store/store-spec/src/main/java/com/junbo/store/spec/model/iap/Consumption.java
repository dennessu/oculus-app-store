/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.store.spec.model.iap;

import com.junbo.common.cloudant.json.annotations.CloudantIgnore;
import com.junbo.common.id.ItemId;
import com.junbo.common.id.UserId;
import com.junbo.common.model.ResourceMeta;

/**
 * Entitlement Consumption for IAP.
 */
public class Consumption extends ResourceMeta<String> {

    private String consumptionId;
    private UserId user;
    private Integer useCountConsumed;
    private String sku;
    private String trackingGuid;
    private ItemId hostItem;
    private String packageName;

    @CloudantIgnore
    private Long signatureTimestamp;

    public UserId getUser() {
        return user;
    }

    public void setUser(UserId user) {
        this.user = user;
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

    public Long getSignatureTimestamp() {
        return signatureTimestamp;
    }

    public void setSignatureTimestamp(Long signatureTimestamp) {
        this.signatureTimestamp = signatureTimestamp;
    }

    public ItemId getHostItem() {
        return hostItem;
    }

    public void setHostItem(ItemId hostItem) {
        this.hostItem = hostItem;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    @Override
    public String getId() {
        return consumptionId;
    }

    @Override
    public void setId(String id) {
        consumptionId = id;
    }
}
