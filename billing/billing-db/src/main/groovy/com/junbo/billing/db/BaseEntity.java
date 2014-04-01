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
    protected Date createdTime;
    protected String createdBy;
    protected Date modifiedTime;
    protected String modifiedBy;

    @Column(name = "created_date")
    @NotNull(message = EntityValidationCode.MISSING_VALUE)
    public Date getCreatedTime() {
        return createdTime;
    }
    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    @Column(name = "created_by")
    @NotEmpty(message = EntityValidationCode.MISSING_VALUE)
    @Length(max=64, message= EntityValidationCode.TOO_LONG)
    public String getCreatedBy() {
        return createdBy;
    }
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Column(name = "modified_date")
    public Date getModifiedTime() {
        return modifiedTime;
    }
    public void setModifiedTime(Date modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    @Column(name = "modified_by")
    @Length(max=64, message= EntityValidationCode.TOO_LONG)
    public String getModifiedBy() {
        return modifiedBy;
    }
    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

}
