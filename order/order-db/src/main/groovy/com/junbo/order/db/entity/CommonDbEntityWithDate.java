/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.entity;

import com.junbo.order.db.ValidationMessages;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by chriszhu on 1/24/14.
 */

@MappedSuperclass
public class CommonDbEntityWithDate implements Serializable {
    protected Date createdTime;
    protected String createdBy;
    protected Date updatedTime;
    protected String updatedBy;
    //protected Integer resourceAge;

    @Column(name = "CREATED_TIME")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public Date getCreatedTime() {
        return createdTime;
    }
    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    @Column(name = "CREATED_BY")
    @NotEmpty(message = ValidationMessages.MISSING_VALUE)
    @Length(max=128, message=ValidationMessages.TOO_LONG)
    public String getCreatedBy() {
        return createdBy;
    }
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Column(name = "UPDATED_TIME")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public Date getUpdatedTime() {
        return updatedTime;
    }
    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }

    @Column(name = "UPDATED_BY")
    @NotEmpty(message = ValidationMessages.MISSING_VALUE)
    @Length(max=128, message=ValidationMessages.TOO_LONG)
    public String getUpdatedBy() {
        return updatedBy;
    }
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

//    @Column(name = "RESOURCE_AGE")
//    @NotEmpty(message = ValidationMessages.MISSING_VALUE)
//    public Integer getRev() {
//        return resourceAge;
//    }
//
//    public void setRev(Integer resourceAge) {
//        this.resourceAge = resourceAge;
//    }
}
