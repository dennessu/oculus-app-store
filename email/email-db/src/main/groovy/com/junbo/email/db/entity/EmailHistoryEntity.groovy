/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.db.entity

import javax.persistence.Entity
import javax.persistence.Table
import javax.persistence.Column
import javax.persistence.Id

/**
 * Entity of EmailHistory
 */
@Entity
@Table(name = 'email_history')
class EmailHistoryEntity extends BaseEntity {
    @Id
    @Column(name = 'id')
    private Long id

    @Column(name = 'user_id')
    private Long userId

    @Column(name='source')
    private String source

    @Column(name='action')
    private String action

    @Column(name='payload')
    private String payload

    @Column(name='recipients')
    private String recipients

    @Column(name='locale')
    private String locale

    @Column(name = 'status')
    private Short status

    @Column(name='status_reason')
    private String statusReason

    @Column(name='retry_Count')
    private Integer retryCount

    @Column(name = 'priority')
    private Integer priority

    @Column(name = 'sent_date')
    private Date sentDate

    @Column(name = 'is_resend')
    private Boolean isResend

    Long getId() {
        return id
    }

    void setId(Long id) {
        this.id = id
    }

    Long getUserId() {
        return userId
    }

    void setUserId(Long userId) {
        this.userId = userId
    }

    String getSource() {
        return source
    }

    void setSource(String source) {
        this.source = source
    }

    String getAction() {
        return action
    }

    void setAction(String action) {
        this.action = action
    }

    String getPayload() {
        return payload
    }

    void setPayload(String payload) {
        this.payload = payload
    }

    String getRecipients() {
        return recipients
    }

    void setRecipients(String recipients) {
        this.recipients = recipients
    }

    String getLocale() {
        return locale
    }

    void setLocale(String locale) {
        this.locale = locale
    }

    Short getStatus() {
        return status
    }

    void setStatus(Short status) {
        this.status = status
    }

    String getStatusReason() {
        return statusReason
    }

    void setStatusReason(String statusReason) {
        this.statusReason = statusReason
    }

    Integer getRetryCount() {
        return retryCount
    }

    void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount
    }

    Integer getPriority() {
        return priority
    }

    void setPriority(Integer priority) {
        this.priority = priority
    }

    Date getSentDate() {
        return sentDate
    }

    void setSentDate(Date sentDate) {
        this.sentDate = sentDate
    }

    Boolean getIsResend() {
        return isResend
    }

    void setIsResend(Boolean isResend) {
        this.isResend = isResend
    }
}
