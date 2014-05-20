/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.junbo.common.cloudant.CloudantEntity;
import com.wordnik.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Null;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Base model.
 */
public class BaseModel implements CloudantEntity {
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

    @JsonIgnore
    private String cloudantId;

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

    @Override
    public String getCloudantId() {
        return cloudantId;
    }

    @Override
    public void setCloudantId(String id) {
        this.cloudantId = id;
    }

    @Override
    public String getCloudantRev() {
        return null;
    }

    @Override
    public void setCloudantRev(String rev) {
    }

    @Override
    public Integer getResourceAge() {
        return rev==null ? null : Integer.parseInt(rev);
    }

    @Override
    public void setResourceAge(Integer resourceAge) {
        this.rev = resourceAge.toString();
    }

    @Override
    public Long getCreatedBy() {
        return null;
    }

    @Override
    public void setCreatedBy(Long createdBy) {

    }

    @Override
    public Long getUpdatedBy() {
        return null;
    }

    @Override
    public void setUpdatedBy(Long updatedBy) {

    }
}
