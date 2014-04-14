/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.common;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

/**
 * Base entity model.
 */
public abstract class BaseEntityModel extends BaseModel {
    @NotNull
    private LocalizableProperty name;

    @JsonProperty("isCurated")
    private Boolean curated;

    private Integer rev;

    public LocalizableProperty getName() {
        return name;
    }

    public void setName(LocalizableProperty name) {
        this.name = name;
    }

    public Boolean getCurated() {
        return curated;
    }

    public void setCurated(Boolean curated) {
        this.curated = curated;
    }

    public abstract Long getCurrentRevisionId();
    public abstract void setCurrentRevisionId(Long currentRevisionId);

    public Integer getRev() {
        return rev;
    }

    public void setRev(Integer rev) {
        this.rev = rev;
    }
}
