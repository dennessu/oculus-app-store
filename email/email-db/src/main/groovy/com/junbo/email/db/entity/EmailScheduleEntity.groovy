/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.db.entity

import javax.persistence.Column
import javax.persistence.Id

/**
 * Entity of EmailSchedule
 */
class EmailScheduleEntity extends BaseEntity {
    @Id
    @Column(name = 'id')
    private Long id

    @Column(name = 'user_id')
    private Long userId

    @Column(name = 'source')
    private String source

    @Column(name = 'action')
    private String action

    @Column(name = 'locale')
    private String locale

    @Column(name = 'payload')
    private String payload

    @Column(name = 'recipient')
    private String recipient

    @Column(name = 'priority')
    private Integer priority

    @Column(name = 'schedule_date')
    private Date scheduleDate

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

    String getLocale() {
        return locale
    }

    void setLocale(String locale) {
        this.locale = locale
    }

    String getPayload() {
        return payload
    }

    void setPayload(String payload) {
        this.payload = payload
    }

    String getRecipient() {
        return recipient
    }

    void setRecipient(String recipient) {
        this.recipient = recipient
    }

    Integer getPriority() {
        return priority
    }

    void setPriority(Integer priority) {
        this.priority = priority
    }

    Date getScheduleDate() {
        return scheduleDate
    }

    void setScheduleDate(Date scheduleDate) {
        this.scheduleDate = scheduleDate
    }
}
