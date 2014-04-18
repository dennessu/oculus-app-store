/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.db.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

/**
 * Entity of EmailSchedule
 */
@Entity
@Table(name = 'email_schedule')
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

    @Column(name = 'recipients')
    private String recipients

    @Column(name = 'priority')
    private Integer priority

    @Column(name = 'schedule_Time')
    private Date scheduleTime

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

    String getRecipients() {
        return recipients
    }

    void setRecipients(String recipients) {
        this.recipients = recipients
    }

    Integer getPriority() {
        return priority
    }

    void setPriority(Integer priority) {
        this.priority = priority
    }

    Date getScheduleTime() {
        return scheduleTime
    }

    void setScheduleTime(Date scheduleTime) {
        this.scheduleTime = scheduleTime
    }
}
