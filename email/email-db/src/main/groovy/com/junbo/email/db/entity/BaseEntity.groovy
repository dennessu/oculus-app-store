/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.db.entity

import javax.persistence.*

/**
 * Base Entity of Email
 */
@MappedSuperclass
abstract class BaseEntity {
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = 'created_time')
    private Date createdTime

    @Column(name = 'created_by')
    private String createdBy

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = 'modified_time')
    private Date updatedTime

    @Column(name = 'modified_by')
    private String updatedBy

    Date getCreatedTime() {
        return createdTime
    }

    void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime
    }

    String getCreatedBy() {
        return createdBy
    }

    void setCreatedBy(String createdBy) {
        this.createdBy = createdBy
    }

    Date getUpdatedTime() {
        return updatedTime
    }

    void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime
    }

    String getUpdatedBy() {
        return updatedBy
    }

    void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy
    }

    @Transient
    abstract Long getId()

    abstract void setId(Long id)
}
