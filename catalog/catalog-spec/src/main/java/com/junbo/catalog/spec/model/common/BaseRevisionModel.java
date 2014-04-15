/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Base entity revision model.
 */
public abstract class BaseRevisionModel extends BaseModel {
    //(Design, PendingReview, Rejected) => (Released, Deleted)
    private String status;

    private Long timestamp;

    @NotNull
    private LocalizableProperty name;

    private Integer rev;

    @JsonUnwrapped
    private ExtensibleProperties properties;

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

    public LocalizableProperty getName() {
        return name;
    }

    public void setName(LocalizableProperty name) {
        this.name = name;
    }

    public Integer getRev() {
        return rev;
    }

    public void setRev(Integer rev) {
        this.rev = rev;
    }

    public ExtensibleProperties getProperties() {
        return properties;
    }

    public void setProperties(ExtensibleProperties properties) {
        this.properties = properties;
    }

    @JsonIgnore
    public abstract Long getEntityId();
}
