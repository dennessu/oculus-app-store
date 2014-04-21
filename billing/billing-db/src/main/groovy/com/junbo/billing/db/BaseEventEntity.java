/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.db;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by xmchen on 14-2-19.
 */
@MappedSuperclass
public class BaseEventEntity implements Serializable {

    @Id
    @Column(name = "event_id")
    private Long eventId;

    @Column(name = "action_type_id")
    private Short actionTypeId;

    @Column(name = "event_date")
    private Date eventDate;

    public Long getEventId() {
        return eventId;
    }
    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Short getActionTypeId() {
        return actionTypeId;
    }
    public void setActionTypeId(Short actionTypeId) {
        this.actionTypeId = actionTypeId;
    }

    public Date getEventDate() {
        return eventDate;
    }
    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }
}
