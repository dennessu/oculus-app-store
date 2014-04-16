/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.db;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by xmchen on 14-1-17.
 */
@MappedSuperclass
public class BaseEntity implements Serializable {
    @Column(name = "created_date")
    protected Date createdTime;

    @Column(name = "created_by")
    protected String createdBy;

    @Column(name = "modified_date")
    protected Date modifiedTime;

    @Column(name = "modified_by")
    protected String modifiedBy;

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

    public Date getModifiedTime() {
        return modifiedTime;
    }
    public void setModifiedTime(Date modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }
    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

}
