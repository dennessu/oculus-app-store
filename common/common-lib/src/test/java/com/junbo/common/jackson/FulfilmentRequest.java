/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.jackson;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.jackson.annotation.FulfilmentRequestId;
import com.junbo.common.jackson.annotation.OrderId;
import com.junbo.common.jackson.serializer.CascadeResourceId;

/**
 * FulfilmentRequest.
 */
public class FulfilmentRequest {
    private Long requestId;

    @OrderId
    private Long orderId;

    public Long getRequestId() {
        return requestId;
    }

    @JsonProperty("requestId")
    @FulfilmentRequestId
    public CascadeResourceId getCascadeRequestId() {
        return new CascadeResourceId(null, new Object[]{orderId});
    }

    @FulfilmentRequestId
    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
}
