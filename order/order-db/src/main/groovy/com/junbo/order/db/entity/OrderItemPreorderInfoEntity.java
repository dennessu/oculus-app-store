/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.entity;

import com.junbo.order.db.ValidationMessages;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by chriszhu on 1/26/14.
 */
@Entity
@Table(name = "ORDER_ITEM_PREORDER_INFO")
public class OrderItemPreorderInfoEntity extends CommonDbEntityWithDate{
    private Long orderItemPreorderInfoId;
    private Long orderItemId;
    private Date billingDate;
    private Date preNotificationDate;
    private Date releaseDate;

    @Id
    @Column(name = "ORDER_ITEM_PREORDER_INFO_ID")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public Long getOrderItemPreorderInfoId() {
        return orderItemPreorderInfoId;
    }

    public void setOrderItemPreorderInfoId(Long orderItemPreorderInfoId) {
        this.orderItemPreorderInfoId = orderItemPreorderInfoId;
    }

    @Column(name = "ORDER_ITEM_ID")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public Long getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(Long orderItemId) {
        this.orderItemId = orderItemId;
    }

    @Column(name = "BILLING_TIME")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public Date getBillingDate() {
        return billingDate;
    }

    public void setBillingDate(Date billingDate) {
        this.billingDate = billingDate;
    }

    @Column(name = "PRE_NOTIFICATION_TIME")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public Date getPreNotificationDate() {
        return preNotificationDate;
    }

    public void setPreNotificationDate(Date preNotificationDate) {
        this.preNotificationDate = preNotificationDate;
    }

    @Column(name = "RELEASE_TIME")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    @Override
    @Transient
    public Long getShardId() {
        return orderItemId;
    }
}
