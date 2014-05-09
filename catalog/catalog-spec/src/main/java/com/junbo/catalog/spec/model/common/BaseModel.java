/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.common;

import com.wordnik.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Null;
import java.util.Date;

/**
 * Base model.
 */
public class BaseModel {
    @ApiModelProperty(position = 1000, required = true,
            value = "[Client Immutable] The revision of the resource. Used for optimistic locking.")
    private String rev;
    @Null
    @ApiModelProperty(position = 1001, required = true,
            value = "[Client Immutable] The created datetime of the resource.")
    private Date createdTime;
    @Null
    @ApiModelProperty(position = 1002, required = true,
            value = "[Client Immutable] The updated datetime of the resource.")
    private Date updatedTime;
    @ApiModelProperty(position = 1003, required = false,
            value = "[Client Immutable] The user who operated the resource.")
    private AdminInfo adminInfo;

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }

    public AdminInfo getAdminInfo() {
        return adminInfo;
    }

    public void setAdminInfo(AdminInfo adminInfo) {
        this.adminInfo = adminInfo;
    }

    public String getRev() {
        return rev;
    }

    public void setRev(String rev) {
        this.rev = rev;
    }
}
