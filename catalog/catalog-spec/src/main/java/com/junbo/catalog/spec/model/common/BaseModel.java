/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.wordnik.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Null;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

    @ApiModelProperty(position = 1004, required = true, value = "The future expansion properties.")
    private Map<String, JsonNode> futureExpansion = new HashMap<>();

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

    public Map<String, JsonNode> getFutureExpansion() {
        return futureExpansion;
    }

    public void setFutureExpansion(Map<String, JsonNode> futureExpansion) {
        this.futureExpansion = futureExpansion;
    }
}
