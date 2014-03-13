/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.spec.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.UserId;
import com.junbo.common.id.EmailId;

import java.util.Date;
import java.util.Map;

/**
 * Email model.
 */
public class Email {
    @JsonProperty("self")
    private EmailId id;

    @JsonProperty("user")
    private UserId userId;

    private String source;

    private String action;

    private String locale;

    private String type;

    private String recipient;

    private String status;

    private String statusReason;

    private Integer retryCount;

    private Integer priority;

    private Date sentDate;

    private Boolean isResend;

    private Date scheduleDate;

    private Map<String,String> properties;

    public EmailId getId() {
        return id;
    }

    public void setId(EmailId id) {
        this.id = id;
    }

    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusReason() {
        return statusReason;
    }

    public void setStatusReason(String statusReason) {
        this.statusReason = statusReason;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Date getSentDate() {
        return sentDate;
    }

    public void setSentDate(Date sentDate) {
        this.sentDate = sentDate;
    }

    public Boolean getIsResend() {
        return isResend;
    }

    public void setIsResend(Boolean isResend) {
        this.isResend = isResend;
    }

    public Date getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(Date scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }
}
