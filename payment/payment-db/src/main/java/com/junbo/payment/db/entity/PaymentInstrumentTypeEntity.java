/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.db.entity;

import javax.persistence.*;
import java.util.Date;

/**
 * payment instrument type entity.
 */
@Entity
@Table(name = "payment_instrument_type")
public class PaymentInstrumentTypeEntity {
    @Id
    @Column(name = "payment_instrument_type_id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "recurring")
    private String recurring;

    @Column(name = "refundable")
    private String refundable;

    @Column(name = "authorizable")
    private String authorizable;

    @Column(name = "defaultable")
    private String defaultable;

    @Column(name = "created_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdTime;

    @Column(name = "created_by")
    private String createdBy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRecurring() {
        return recurring;
    }

    public void setRecurring(String recurring) {
        this.recurring = recurring;
    }

    public String getRefundable() {
        return refundable;
    }

    public void setRefundable(String refundable) {
        this.refundable = refundable;
    }

    public String getAuthorizable() {
        return authorizable;
    }

    public void setAuthorizable(String authorizable) {
        this.authorizable = authorizable;
    }

    public String getDefaultable() {
        return defaultable;
    }

    public void setDefaultable(String defaultable) {
        this.defaultable = defaultable;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

}

