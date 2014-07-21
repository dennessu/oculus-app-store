/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.purchase;

import com.junbo.common.id.OrderId;
import com.junbo.store.spec.model.BaseResponse;

/**
 * The MakeFreePurchaseResponse class.
 */
public class MakeFreePurchaseResponse extends BaseResponse {

    private OrderId orderId;

    private AppDeliveryData appDeliveryData;

    public OrderId getOrderId() {
        return orderId;
    }

    public void setOrderId(OrderId orderId) {
        this.orderId = orderId;
    }

    public AppDeliveryData getAppDeliveryData() {
        return appDeliveryData;
    }

    public void setAppDeliveryData(AppDeliveryData appDeliveryData) {
        this.appDeliveryData = appDeliveryData;
    }
}
