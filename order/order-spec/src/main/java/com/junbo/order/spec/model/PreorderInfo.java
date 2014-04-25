/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.spec.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.junbo.common.id.OrderItemId;
import com.junbo.common.id.PreorderId;
import com.junbo.common.model.ResourceMeta;

import java.util.Date;
import java.util.List;

/**
 * Created by LinYi on 2/10/14.
 */
public class PreorderInfo extends ResourceMeta {
    @JsonIgnore
    private PreorderId preorderInfoId;
    private Date billingTime;
    private Date preNotificationTime;
    private Date releaseTime;
    private List<PreorderUpdateHistory> updateHistory;
    @JsonIgnore
    private OrderItemId orderItemId;

    public PreorderId getPreorderInfoId() {
        return preorderInfoId;
    }

    public void setPreorderInfoId(PreorderId preorderInfoId) {
        this.preorderInfoId = preorderInfoId;
    }

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

    public OrderItemId getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(OrderItemId orderItemId) {
        this.orderItemId = orderItemId;
    }
}

