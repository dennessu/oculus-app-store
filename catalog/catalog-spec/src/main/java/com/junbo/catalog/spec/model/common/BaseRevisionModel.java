/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.common;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wordnik.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Null;

/**
 * Base entity revision model.
 */
public abstract class BaseRevisionModel extends BaseModel {
    @ApiModelProperty(position = 10, required = true, value = "Status of the revision",
            allowableValues = "DRAFT, PENDING_REVIEW, APPROVED, REJECTED")
    private String status;

    @JsonIgnore
    private Long timestamp;

    public String getStatus() {
        return status;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @JsonIgnore
    public abstract String getEntityId();

    // workaround fastjson de-serialize issue
    @Null
    @JsonIgnore
    @JSONField(serialize = false)
    private transient String id;
}
