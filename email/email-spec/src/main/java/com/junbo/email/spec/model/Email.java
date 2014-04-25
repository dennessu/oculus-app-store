/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.spec.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.junbo.common.id.EmailTemplateId;
import com.junbo.common.id.UserId;
import com.junbo.common.id.EmailId;
//import com.junbo.common.model.ResourceMeta;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Email model.
 */
@JsonPropertyOrder(value = {"self","user","template","recipients","status",
        "priority","sentTime","scheduleTime","replacements"})
public class Email extends Model {
    @ApiModelProperty(position = 1, required = true, value = "[Client Immutable] The id of email resource.")
    @JsonProperty("self")
    private EmailId id;

    @ApiModelProperty(position = 2, required = true, value = "The user resource.")
    @JsonProperty("user")
    private UserId userId;

    @ApiModelProperty(position = 3, required = true, value = "The template id of the email.")
    @JsonProperty("template")
    private EmailTemplateId templateId;

    @ApiModelProperty(position = 5, required = true, value = "The recipients of the email.")
    private List<String> recipients;

    @ApiModelProperty(position = 6, required = true, value = "[Client Immutable] The status of the email.")
    private String status;

    @JsonIgnore
    private String statusReason;

    @JsonIgnore
    private Integer retryCount;

    @ApiModelProperty(position = 7, required = true, value = "The priority of the email.")
    private Integer priority;

    @ApiModelProperty(position = 8, required = true, value = "[Client Immutable] The sentTime of the email.")
    private Date sentTime;

    @JsonIgnore
    private Boolean isResend;

    @ApiModelProperty(position = 9, required = true, value = "The scheduleTime of the email.")
    private Date scheduleTime;

    @ApiModelProperty(position = 10, required = true, value = "The replacements of the email.")
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

    public EmailTemplateId getTemplateId() {
        return templateId;
    }

    public void setTemplateId(EmailTemplateId templateId) {
        this.templateId = templateId;
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

    public Boolean getIsResend() {
        return isResend;
    }

    public void setIsResend(Boolean isResend) {
        this.isResend = isResend;
    }

    public List<String> getRecipients() {
        return recipients;
    }

    public void setRecipients(List<String> recipients) {
        this.recipients = recipients;
    }

    public Date getSentTime() {
        return sentTime;
    }

    public void setSentTime(Date sentTime) {
        this.sentTime = sentTime;
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
