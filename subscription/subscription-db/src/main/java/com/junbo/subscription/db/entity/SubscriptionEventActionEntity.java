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
@Table(name = "SUBSCRIPTION_EVENT_ACTION")
public class SubscriptionEventActionEntity extends Entity {

    private Long subscriptionId;
    private Integer subsEventId;
    private Integer subsActionId;
    private Integer actionTypeId;
    private Integer actionStatusId;
    private String request;
    private String response;

    @Id
    @Column(name = "subscription_Id")
    public Long getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(Long subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    @Id
    @Column(name = "subs_event_id")
    public Integer getSubsEventId() {
        return subsEventId;
    }

    public void setSubsEventId(Integer subsEventId) {
        this.subsEventId = subsEventId;
    }

    @Id
    @Column(name = "subs_action_id")
    public Integer getSubsActionId() {
        return subsActionId;
    }

    public void setSubsActionId(Integer subsActionId) {
        this.subsActionId = subsActionId;
    }

    @Id
    @Column(name = "action_type_id")
    public Integer getActionTypeId() {
        return actionTypeId;
    }

    public void setActionTypeId(Integer actionTypeId) {
        this.actionTypeId = actionTypeId;
    }

    @Id
    @Column(name = "action_status_id")
    public Integer getActionStatusId() {
        return actionStatusId;
    }

    public void setActionStatusId(Integer actionStatusId) {
        this.actionStatusId = actionStatusId;
    }

    @Id
    @Column(name = "request")
    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    @Id
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
        return this.subscriptionId;
    }

    @Override
    public void setId(Long id) {
        this.subscriptionId = id;
    }

    @Override
    @Transient
    public Long getShardMasterId() {
        return subscriptionId;
    }

}
