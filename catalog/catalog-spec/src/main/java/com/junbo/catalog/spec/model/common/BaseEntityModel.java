/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Base entity model.
 */
public abstract class BaseEntityModel extends BaseModel {
   /* @NotNull
    @ApiModelProperty(position = 10, required = true, value = "Friendly identifier")
    @JsonProperty("friendlyIdentifier")
    private String name;
*/
    @ApiModelProperty(position = 11, required = true, value = "Curated")
    @JsonProperty("isCurated")
    private Boolean curated;

    @ApiModelProperty(position = 1003, required = true, value = "[Client Immutable] rev")
    private String rev;

    public Boolean getCurated() {
        return curated;
    }

    public void setCurated(Boolean curated) {
        this.curated = curated;
    }

    public abstract Long getCurrentRevisionId();
    public abstract void setCurrentRevisionId(Long currentRevisionId);

    public String getRev() {
        return rev;
    }

    public void setRev(String rev) {
        this.rev = rev;
    }
}
