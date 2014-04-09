/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.jackson.annotation.UserId;

import javax.validation.constraints.NotNull;

/**
 * Base entity model.
 */
public abstract class BaseEntityModel extends BaseModel {
    @NotNull
    private String name;

    private Boolean curated;
    @UserId
    @JsonProperty("developer")
    private Long ownerId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getCurated() {
        return curated;
    }

    public void setCurated(Boolean curated) {
        this.curated = curated;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public abstract Long getCurrentRevisionId();
    public abstract void setCurrentRevisionId(Long currentRevisionId);
}
