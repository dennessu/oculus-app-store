/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.iap;

/**
 * The IAPEntitlementConsumeRequest class.
 */
public class IAPConsumeItemRequest {

    private String sku;

    private Integer useCountConsumed;

    private String trackingGuid;

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
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
}
