/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.spec.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.junbo.common.id.OrderItemId;

import java.util.Date;
import java.util.List;

/**
 * Created by LinYi on 2/10/14.
 */
public class PreorderInfo extends BaseModelWithDate {
    private Long id;
    private Date billingTime;
    private Date preNotificationTime;
    private Date releaseTime;
    private List<PreorderUpdateHistory> updateHistory;
    private OrderItemId orderItem;

    public Date getBillingTime() {
        return billingTime;
    }

    public void setBillingTime(Date billingTime) {
        this.billingTime = billingTime;
    }

    public Date getPreNotificationTime() {
        return preNotificationTime;
    }

    public void setPreNotificationTime(Date preNotificationTime) {
        this.preNotificationTime = preNotificationTime;
    }

    public Date getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(Date releaseTime) {
        this.releaseTime = releaseTime;
    }

    public List<PreorderUpdateHistory> getUpdateHistory() {
        return updateHistory;
    }

    public void setUpdateHistory(List<PreorderUpdateHistory> updateHistory) {
        this.updateHistory = updateHistory;
    }

    @JsonIgnore
    public OrderItemId getOrderItem() {
        return orderItem;
    }

    public void setOrderItem(OrderItemId orderItem) {
        this.orderItem = orderItem;
    }

    @JsonIgnore
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}

