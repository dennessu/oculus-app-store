/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.junbo.common.cloudant.CloudantEntity;
import com.junbo.common.cloudant.json.annotations.CloudantIgnore;
import com.junbo.common.cloudant.json.annotations.CloudantProperty;
import com.junbo.common.jackson.annotation.UserId;
import com.junbo.common.jackson.deserializer.IntFromStringDeserializer;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * The base class for all resource with system properties.
 */
public abstract class ResourceMeta implements CloudantEntity {

    @ApiModelProperty(position = 1000, required = true,
            value = "[Client Immutable] The revision of the resource. Used for optimistic locking.")
    @JsonProperty("rev")
    @JsonSerialize(using = ToStringSerializer.class)
    @JsonDeserialize(using = IntFromStringDeserializer.class)
    private Integer resourceAge;

    @ApiModelProperty(position = 1001, required = true,
            value = "[Client Immutable] The created datetime of the resource.")
    private Date createdTime;

    @ApiModelProperty(position = 1002, required = true,
            value = "[Client Immutable] The updated datetime of the resource.")
    private Date updatedTime;

    @ApiModelProperty(position = 1003, required = false,
            value = "[Client Immutable] The created datetime of the resource.")
    @CloudantIgnore
    private AdminInfo adminInfo;

    @JsonIgnore
    @UserId
    private Long createdBy;

    @JsonIgnore
    @UserId
    private Long updatedBy;

    @JsonIgnore
    private String createdByClient;

    @JsonIgnore
    private String updatedByClient;

    @JsonIgnore
    @CloudantProperty("_id")
    private String cloudantId;

    @JsonIgnore
    @CloudantProperty("_rev")
    private String cloudantRev;

    public Integer getResourceAge() {
        return resourceAge;
    }

    public void setResourceAge(Integer resourceAge) {
        this.resourceAge = resourceAge;
    }

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

    public String getCloudantId() {
        return cloudantId;
    }

    public void setCloudantId(String cloudantId) {
        this.cloudantId = cloudantId;
    }

    public String getCloudantRev() {
        return cloudantRev;
    }

    public void setCloudantRev(String cloudantRev) {
        this.cloudantRev = cloudantRev;
    }

    public AdminInfo getAdminInfo() {
        return adminInfo;
    }

    public void setAdminInfo(AdminInfo adminInfo) {
        this.adminInfo = adminInfo;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Long getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getCreatedByClient() {
        return createdByClient;
    }

    public void setCreatedByClient(String createdByClient) {
        this.createdByClient = createdByClient;
    }

    public String getUpdatedByClient() {
        return updatedByClient;
    }

    public void setUpdatedByClient(String updatedByClient) {
        this.updatedByClient = updatedByClient;
    }
}
