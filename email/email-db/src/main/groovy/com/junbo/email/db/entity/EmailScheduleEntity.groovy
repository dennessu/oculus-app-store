/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.db.entity

import groovy.transform.CompileStatic

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

/**
 * Entity of EmailSchedule
 */
@CompileStatic
@Entity
@Table(name = 'email_schedule')
class EmailScheduleEntity extends BaseEntity {
    @Id
    @Column(name = 'id')
    private Long id

    @Column(name = 'user_id')
    private Long userId

    @Column(name = 'template_id')
    private Long templateId

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

    Long getTemplateId() {
        return templateId
    }

    void setTemplateId(Long templateId) {
        this.templateId = templateId
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
