/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.subscription.db.entity;

import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;
import java.util.UUID;


/**
 * subscription entity.
 */
@javax.persistence.Entity
@Table(name = "subscription")
public class SubscriptionEntity extends Entity {
    private Long subscriptionId;

    private UUID trackingUuid;
    private Long userId;
    private Long piId;
    private String country;
    private String currency;
    private SubscriptionStatus statusId;
    private String itemId;
    private Date subsStartDate;
    private Date subsEndDate;
    private Integer anniversaryDay;
    private String source;
    private String billingMode;

    @Id
    @Column(name = "subscription_id")
    public Long getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(Long subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    @Column(name = "tracking_uuid")
    @Type(type = "pg-uuid")
    public UUID getTrackingUuid() { return trackingUuid; }

    public void setTrackingUuid(UUID trackingUuid) { this.trackingUuid = trackingUuid; }


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

    @Column(name = "country_code")
    public String getCountry() { return country; }

    public void setCountry(String country) {
        this.country = country;
    }

    @Column(name = "currency_code")
    public String getCurrency() { return currency; }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Column(name = "status_id")
    public SubscriptionStatus getStatusId() {
        return statusId;
    }

    public void setStatusId(SubscriptionStatus statusId) {
        this.statusId = statusId;
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

    @Column(name = "anniversary_day")
    public Integer getAnniversaryDay() { return anniversaryDay; }

    public void setAnniversaryDay(Integer anniversaryDay) { this.anniversaryDay = anniversaryDay; }

    @Column(name = "source")
    public String getSource() { return source; }

    public void setSource(String source) { this.source = source; }

    @Column(name = "billing_mode")
    public String getBillingMode() { return billingMode; }

    public void setBillingMode(String billingMode) {
        this.billingMode = billingMode;
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

}
