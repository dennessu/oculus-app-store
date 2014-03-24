/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.subscription.db.entity;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;


/**
 * subscription entity.
 */
@javax.persistence.Entity
@Table(name = "SUBSCRIPTION")
public class SubscriptionEntity extends Entity {
    private Long subscriptionId;
    private Long userId;
    private Long piId;
    private Long statusId;
    private String itemId;
    private Date subsStartDate;
    private Date subsEndDate;
    private String partnerId;

    @Id
    @Column(name = "subscription_Id")
    public Long getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(Long subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    @Column(name = "user_id")
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Column(name = "pi_id")
    public Long getPiId() { return piId; }

    public void setPiId(Long piId) {
        this.piId = piId;
    }

    @Column(name = "status_id")
    public Long getStatusId() {
        return statusId;
    }

    public void setStatusId(Long statusId) {
        this.statusId = statusId;
    }

    @Transient
    @Override
    public Long getId() {
        return this.subscriptionId;
    }

    @Override
    public void setId(Long id) {
        this.subscriptionId = id;
    }

    @Override
    @Transient
    public Long getShardMasterId() {
        return userId;
    }

    @Column(name = "item_id")
    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    @Column(name = "subs_start_date")
    public Date getSubsStartDate() {
        return subsStartDate;
    }

    public void setSubsStartDate(Date subsStartDate) {
        this.subsStartDate = subsStartDate;
    }

    @Column(name = "subs_end_date")
    public Date getSubsEndDate() {
        return subsEndDate;
    }

    public void setSubsEndDate(Date subsEndDate) {
        this.subsEndDate = subsEndDate;
    }

    @Column(name = "partner_id")
    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }
}
