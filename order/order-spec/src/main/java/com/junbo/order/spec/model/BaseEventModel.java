/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.spec.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.junbo.common.model.ResourceMetaForDualWrite;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.UUID;

/**
 * Created by chriszhu on 2/26/14.
 */
public abstract class BaseEventModel<K> extends ResourceMetaForDualWrite<K> {
    @ApiModelProperty(required = true, position = 100, value = "The order-event action. " +
            "RATE: rate the order, " +
            "CHARGE: charge the order, " +
            "FULFILL: fulfill the order, " +
            "AUTHORIZE: authorize the order," +
            "REFUND: refund the order, " +
            "CANCEL: cancel the order, " +
            "PREORDER: preorder the order, " +
            "PARTIAL_REFUND: partial refund the order, " +
            "CAPTURE: capture the authorized order amount, " +
            "PARTIAL_CHARGE: partial charge the orde. ",
            allowableValues = "RATE, CHARGE, FULFILL, AUTHORIZE, REFUND, " +
                    "CANCEL, PREORDER, PARTIAL_REFUND, CAPTURE, PARTIAL_CHARGE")
    private String action;
    @ApiModelProperty(required = true, position = 110, value = "The order-event status. " +
            "OPEN: init status. " +
            "PROCESSING: being processed at server side, " +
            "PENDING: pending on user action. e.g: web payment, " +
            "COMPLETED: completed, " +
            "FAILED: failed with expected reason (e.g: invalid credit card), " +
            "ERROR: failed with unexpected reason. ",
            allowableValues = "OPEN, PROCESSING, PENDING, COMPLETED, FAILED, ERROR")
    private String status;
    @JsonIgnore
    private UUID trackingUuid;
    @ApiModelProperty(required = true, position = 120, value = "The order-event properties, json format. ")
    private String properties;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public UUID getTrackingUuid() {
        return trackingUuid;
    }

    public void setTrackingUuid(UUID trackingUuid) {
        this.trackingUuid = trackingUuid;
    }

    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }
}
