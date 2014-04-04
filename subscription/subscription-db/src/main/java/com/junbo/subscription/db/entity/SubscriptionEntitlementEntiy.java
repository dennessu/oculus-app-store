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

/**
 * Created by Administrator on 14-4-4.
 */
@javax.persistence.Entity
@Table(name = "subscription_entitlement")
public class SubscriptionEntitlementEntiy extends Entity {

    private Long subsEntid;
    private Long subscriptionId;
    private Long entitlementId;
    private int entitlementStatus;

    @Id
    @Column(name = "id")
    public Long getSubsEntId() {
        return subsEntid;
    }

    public void setSubsEntId(Long subsEntid) {
        this.subsEntid = subsEntid;
    }

    @Column(name = "subscription_id")
    public Long getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(Long subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    @Column(name = "entitlement_id")
    public Long getEntitlementId() {
        return entitlementId;
    }

    public void setEntitlementId(Long entitlementId) {
        this.entitlementId = entitlementId;
    }

    @Column(name = "entitlement_status")
    public int getEntitlementStatus() {
        return entitlementStatus;
    }

    public void setEntitlementStatus(int entitlementStatus) {
        this.entitlementStatus = entitlementStatus;
    }

    @Transient
    @Override
    public Long getId() {
        return this.subsEntid;
    }

    @Override
    public void setId(Long subsEntid) {
        this.subsEntid = subsEntid;
    }

    @Transient
    @Override
    public Long getShardMasterId() {
        return subscriptionId;
    }

}
