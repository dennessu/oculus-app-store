/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model.migration;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.error.AppError;
import com.junbo.common.id.OrganizationId;
import com.junbo.common.id.UserId;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Created by liangfu on 6/10/14.
 */
public class OculusOutput {

    @ApiModelProperty(position = 1, required = false, value = "Migrated user id")
    @JsonProperty("user")
    private UserId userId;

    @ApiModelProperty(position = 2, required = false, value = "Migrated user's organization")
    @JsonProperty("organization")
    private OrganizationId organizationId;

    @ApiModelProperty(position = 3, required = false, value = "Migrated error during migration.")
    @JsonProperty("error")
    private AppError error;

    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
    }

    public OrganizationId getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(OrganizationId organizationId) {
        this.organizationId = organizationId;
    }

    public AppError getError() {
        return error;
    }

    public void setError(AppError error) {
        this.error = error;
    }
}
