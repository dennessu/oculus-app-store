/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.entity;

import com.junbo.order.db.ValidationMessages;
import com.junbo.order.spec.model.enums.EventStatus;
import com.junbo.order.spec.model.enums.SubledgerActionType;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

/**
 * Created by chriszhu on 1/29/14.
 */
@Entity
@Table(name = "SUBLEDGER_EVENT")
public class SubledgerEventEntity extends CommonEventEntity{

    private Long subledgerId;
    private SubledgerActionType actionId;
    private EventStatus statusId;
    private String properties;

    @Column(name = "SUBLEDGER_ID")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public Long getSubledgerId() {
        return subledgerId;
    }

    public void setSubledgerId(Long subledgerId) {
        this.subledgerId = subledgerId;
    }

    @Column (name = "ACTION_ID")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    @Type(type = "com.junbo.order.db.entity.type.SubledgerActionEnumType")
    public SubledgerActionType getActionId() {
        return actionId;
    }

    public void setActionId(SubledgerActionType actionId) {
        this.actionId = actionId;
    }

    @Column (name = "STATUS_ID")
    @NotNull (message = ValidationMessages.MISSING_VALUE)
    @Type(type = "com.junbo.order.db.entity.type.EventStatusType")
    public EventStatus getStatusId() {
        return statusId;
    }

    public void setStatusId(EventStatus statusId) {
        this.statusId = statusId;
    }

    @Column(name = "PROPERTIES")
    @Length(max=4000, message=ValidationMessages.TOO_LONG)
    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }

    @Override
    @Transient
    public Long getShardId() {
        return getEventId();
    }
}
