// CHECKSTYLE:OFF
/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.model.users;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.junbo.common.json.PropertyAssignedAware;
import com.junbo.common.json.PropertyAssignedAwareSupport;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * Resource meta.
 * Per resource will have those attributes.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class ResourceMeta implements PropertyAssignedAware {

    protected final PropertyAssignedAwareSupport support = new PropertyAssignedAwareSupport();

    @ApiModelProperty(position = 1000, required = true, value = "The resource version.")
    private Integer resourceAge;

    @ApiModelProperty(position = 1001, required = false, value = "The created time of the resource.")
    private Date createdTime;

    @ApiModelProperty(position = 1002, required = false, value = "The lastest updated time of the resource.")
    private Date updatedTime;

    @JsonIgnore
    private String createdBy;

    @JsonIgnore
    private String updatedBy;

    @JsonIgnore
    private String _id;

    @JsonIgnore
    private String _rev;

    public String get_id() {
        return _id;
    }

    public void set_id(String id) {
        this._id = id;
    }

    public String get_rev() {
        return _rev;
    }

    public void set_rev(String rev) {
        this._rev = rev;
    }

    public Integer getResourceAge() {
        return resourceAge;
    }

    public void setResourceAge(Integer resourceAge) {
        this.resourceAge = resourceAge;
        support.setPropertyAssigned("resourceAge");
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
        support.setPropertyAssigned("createdTime");
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
        support.setPropertyAssigned("updatedTime");
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
        support.setPropertyAssigned("createdBy");
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
        support.setPropertyAssigned("updatedBy");
    }

    @Override
    public boolean isPropertyAssigned(String propertyName) {
        return support.isPropertyAssigned(propertyName);
    }
}
