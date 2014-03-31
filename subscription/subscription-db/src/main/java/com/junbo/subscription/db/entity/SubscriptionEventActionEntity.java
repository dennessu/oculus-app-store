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
 * Created by Administrator on 14-3-28.
 */
@javax.persistence.Entity
@Table(name = "subscription_event_action")
public class SubscriptionEventActionEntity extends Entity {

    private Long subscriptionId;
    private Long subsEventId;
    private Long subsActionId;
    private SubscriptionActionType actionTypeId;
    private SubscriptionStatus actionStatusId;
    private String request;
    private String response;

    @Column(name = "subscription_id")
    public Long getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(Long subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    @Column(name = "subs_event_id")
    public Long getSubsEventId() {
        return subsEventId;
    }

    public void setSubsEventId(Long subsEventId) {
        this.subsEventId = subsEventId;
    }

    @Id
    @Column(name = "subs_action_id")
    public Long getSubsActionId() {
        return subsActionId;
    }

    public void setSubsActionId(Long subsActionId) {
        this.subsActionId = subsActionId;
    }

    @Column(name = "action_type_id")
    public SubscriptionActionType getActionTypeId() {
        return actionTypeId;
    }

    public void setActionTypeId(SubscriptionActionType actionTypeId) {
        this.actionTypeId = actionTypeId;
    }

    @Column(name = "action_status_id")
    public SubscriptionStatus getActionStatusId() {
        return actionStatusId;
    }

    public void setActionStatusId(SubscriptionStatus actionStatusId) {
        this.actionStatusId = actionStatusId;
    }

    @Column(name = "request")
    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    @Column(name = "response")
    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    @Transient
    @Override
    public Long getId() {
        return this.subsActionId;
    }

    @Override
    public void setId(Long id) {
        this.subsActionId = id;
    }

    @Override
    @Transient
    public Long getShardMasterId() {
        return subscriptionId;
    }

}
