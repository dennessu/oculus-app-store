/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.db;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by xmchen on 14-2-19.
 */
@MappedSuperclass
public class BaseEventEntity implements Serializable {
    private Long eventId;
    private Short actionTypeId;
    private Short statusId;
    private Date eventDate;

    @Id
    @Column(name = "event_id")
    public Long getEventId() {
        return eventId;
    }
    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    @Column(name = "action_type_id")
    @NotNull(message = EntityValidationCode.MISSING_VALUE)
    public Short getActionTypeId() {
        return actionTypeId;
    }
    public void setActionTypeId(Short actionTypeId) {
        this.actionTypeId = actionTypeId;
    }

    @Column(name = "status_id")
    @NotNull(message = EntityValidationCode.MISSING_VALUE)
    public Short getStatusId() {
        return statusId;
    }
    public void setStatusId(Short statusId) {
        this.statusId = statusId;
    }

    @Column(name = "event_date")
    @NotNull(message = EntityValidationCode.MISSING_VALUE)
    public Date getEventDate() {
        return eventDate;
    }
    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }
}
