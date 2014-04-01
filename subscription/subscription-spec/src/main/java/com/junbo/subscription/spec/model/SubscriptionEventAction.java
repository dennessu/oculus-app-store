/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.subscription.spec.model;

/**
 * Created by Administrator on 14-3-31.
 */
public class SubscriptionEventAction {

    private Long subscriptionEventId;

    private Long subscriptionActionId;

    private String actionType;

    private String actionStatus;

    private String request;

    private String response;

    private Long subscriptionId;

    public Long getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(Long subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public Long getSubscriptionEventId() {
        return subscriptionEventId;
    }

    public void setSubscriptionEventId(Long subscriptionEventId) {
        this.subscriptionEventId = subscriptionEventId;
    }

    public Long getSubscriptionActionId() {
        return subscriptionActionId;
    }

    public void setSubscriptionActionId(Long subscriptionActionId) {
        this.subscriptionActionId = subscriptionActionId;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getActionStatus() {
        return actionStatus;
    }

    public void setActionStatus(String actionStatus) {
        this.actionStatus = actionStatus;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

}
