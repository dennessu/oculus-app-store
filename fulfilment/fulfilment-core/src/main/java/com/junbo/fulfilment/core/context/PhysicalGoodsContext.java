/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.core.context;

import com.junbo.fulfilment.common.collection.Counter;

/**
 * PhysicalGoodsContext.
 */
public class PhysicalGoodsContext extends FulfilmentContext {
    private Long shippingAddressId;
    private String shippingMethodId;
    private Counter shipment;

    public Long getShippingAddressId() {
        return shippingAddressId;
    }

    public void setShippingAddressId(Long shippingAddressId) {
        this.shippingAddressId = shippingAddressId;
    }

    public String getShippingMethodId() {
        return shippingMethodId;
    }

    public void setShippingMethodId(String shippingMethodId) {
        this.shippingMethodId = shippingMethodId;
    }

    public Counter getShipment() {
        return shipment;
    }

    public void setShipment(Counter shipment) {
        this.shipment = shipment;
    }
}
