/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.common;

import com.junbo.common.jackson.annotation.UserId;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Admin info.
 */
public class AdminInfo {
    @ApiModelProperty(position = 1, required = false,
            value = "[Client Immutable] The user who created the resource.")
    @UserId
    private Long createdBy;

    @ApiModelProperty(position = 2, required = false,
            value = "[Client Immutable] The user who updated the resource.")
    @UserId
    private Long updatedBy;

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
}
