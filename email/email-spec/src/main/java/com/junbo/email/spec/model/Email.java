/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.spec.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.junbo.common.id.UserId;
import com.junbo.common.id.EmailId;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Email model.
 */
@JsonPropertyOrder(value = {"self","user","source","action","locale","type","recipients","status","statusReason",
        "retryCount","priority","sentTime","isResend","scheduleTime","replacements","createdTime","modifiedTime"})
public class Email extends Model{
    @JsonProperty("self")
    private EmailId id;

    @JsonProperty("user")
    private UserId userId;

    private String source;

    private String action;

    private String locale;

    private List<String> recipients;

    private String status;

    private String statusReason;

    private Integer retryCount;

    private Integer priority;

    private Date sentTime;

    private Boolean isResend;

    private Date scheduleTime;

    private Map<String,String> replacements;

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

    public List<String> getRecipients() {
        return recipients;
    }

    public void setRecipients(List<String> recipients) {
        this.recipients = recipients;
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

    public Date getSentTime() {
        return sentTime;
    }

    public void setSentTime(Date sentTime) {
        this.sentTime = sentTime;
    }

    public Boolean getIsResend() {
        return isResend;
    }

    public void setIsResend(Boolean isResend) {
        this.isResend = isResend;
    }

    public Date getScheduleTime() {
        return scheduleTime;
    }

    public void setScheduleTime(Date scheduleTime) {
        this.scheduleTime = scheduleTime;
    }

    public Map<String, String> getReplacements() {
        return replacements;
    }

    public void setReplacements(Map<String, String> replacements) {
        this.replacements = replacements;
    }
}
