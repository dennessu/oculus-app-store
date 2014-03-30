/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.jackson.annotation.UserId;

import java.util.Map;

/**
 * Base entity revision model.
 */
public abstract class BaseRevisionModel extends BaseModel {
    @JsonProperty("self")
    private Long revisionId;

    //(Design, PendingReview, Rejected) => (Released, Deleted)
    private String status;
    @UserId
    @JsonProperty("developer")
    private Long ownerId;

    private Map<String, Object> properties;

    public Long getRevisionId() {
        return revisionId;
    }

    public void setRevisionId(Long revisionId) {
        this.revisionId = revisionId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    @JsonIgnore
    public abstract Long getEntityId();
}
