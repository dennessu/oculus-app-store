/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.spec.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.BalanceId;
import com.junbo.common.id.OrderEventId;
import com.junbo.common.id.OrderId;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.UUID;

/**
 * Created by LinYi on 2/10/14.
 */
public class OrderEvent extends BaseEventModel<OrderEventId> {

    /**
     * The BillingInfo class.
     */
    public static class BillingInfo {

        public BillingInfo() {
        }

        public BillingInfo(BalanceId balance) {
            this.balance = balance;
        }

        private BalanceId balance;

        public BalanceId getBalance() {
            return balance;
        }

        public void setBalance(BalanceId balance) {
            this.balance = balance;
        }
    }

    @ApiModelProperty(required = true, position = 10, value = "[Client Immutable] The order-event id.")
    @JsonProperty("self")
    private OrderEventId id;

    @ApiModelProperty(required = true, position = 20, value = "[Client Immutable] The linked order.")
    private OrderId order;

    @JsonIgnore
    private UUID eventTrackingUuid;

    @JsonIgnore
    private String flowName;

    private BillingInfo billingInfo;

    public OrderEventId getId() {
        return id;
    }

    public void setId(OrderEventId id) {
        this.id = id;
    }

    public OrderId getOrder() {
        return order;
    }

    public void setOrder(OrderId order) {
        this.order = order;
    }

    public UUID getEventTrackingUuid() {
        return eventTrackingUuid;
    }

    public void setEventTrackingUuid(UUID eventTrackingUuid) {
        this.eventTrackingUuid = eventTrackingUuid;
    }

    public String getFlowName() {
        return flowName;
    }

    public void setFlowName(String flowName) {
        this.flowName = flowName;
    }

    public BillingInfo getBillingInfo() {
        return billingInfo;
    }

    public void setBillingInfo(BillingInfo billingInfo) {
        this.billingInfo = billingInfo;
    }
}
