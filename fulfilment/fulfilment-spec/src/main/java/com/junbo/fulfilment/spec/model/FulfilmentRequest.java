/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.spec.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.jackson.annotation.*;

import javax.validation.constraints.Null;
import java.util.List;

/**
 * FulfilmentRequest.
 */
public class FulfilmentRequest {
    @Null
    @FulfilmentRequestId
    @JsonProperty("self")
    private Long requestId;

    private String trackingUuid;
    private String requester;

    @UserId
    private Long userId;

    @OrderId
    private Long orderId;

    @ShippingAddressId
    private Long shippingAddressId;

    @ShippingToNameId
    private Long shippingToNameId;

    @ShippingToPhoneId
    private Long shippingToPhoneId;

    @ShippingMethodId
    private String shippingMethodId;

    private List<FulfilmentItem> items;

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public String getTrackingUuid() {
        return trackingUuid;
    }

    public void setTrackingUuid(String trackingGuid) {
        this.trackingUuid = trackingGuid;
    }

    public String getRequester() {
        return requester;
    }

    public void setRequester(String requester) {
        this.requester = requester;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public List<FulfilmentItem> getItems() {
        return items;
    }

    public void setItems(List<FulfilmentItem> items) {
        this.items = items;
    }

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

    public Long getShippingToNameId() {
        return shippingToNameId;
    }

    public void setShippingToNameId(Long shippingToNameId) {
        this.shippingToNameId = shippingToNameId;
    }

    public Long getShippingToPhoneId() {
        return shippingToPhoneId;
    }

    public void setShippingToPhoneId(Long shippingToPhoneId) {
        this.shippingToPhoneId = shippingToPhoneId;
    }
}
