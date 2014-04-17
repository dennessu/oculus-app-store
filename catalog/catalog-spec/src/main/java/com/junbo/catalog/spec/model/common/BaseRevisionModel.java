/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Base entity revision model.
 */
public abstract class BaseRevisionModel extends BaseModel {
    @ApiModelProperty(position = 10, required = true, value = "Status of the item revision")
    private String status;

    @JsonIgnore
    private Long timestamp;

    @ApiModelProperty(position = 1001, required = true, value = "[Client Immutable] rev")
    private String rev;

    //@JsonUnwrapped
    //private ExtensibleProperties properties;

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

    public String getRev() {
        return rev;
    }

    public void setRev(String rev) {
        this.rev = rev;
    }

    @JsonIgnore
    public abstract Long getEntityId();
}
