/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.junbo.common.id.UserId;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * The information which is supposed to be seen only by superuser...
 */
public class AdminInfo {

    @ApiModelProperty(position = 1, required = false,
            value = "[Client Immutable] The user who created the resource.")
    private UserId createdBy;

    @ApiModelProperty(position = 2, required = false,
            value = "[Client Immutable] The user who updated the resource.")
    private UserId updatedBy;

    @JsonIgnore
    private String createdByClient;

    @JsonIgnore
    private String updatedByClient;

    public UserId getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UserId createdBy) {
        this.createdBy = createdBy;
    }

    public UserId getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(UserId updatedBy) {
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
