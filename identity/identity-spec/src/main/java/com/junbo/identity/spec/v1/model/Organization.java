/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.OrganizationId;
import com.junbo.common.id.UserId;
import com.junbo.common.model.PropertyAssignedAwareResourceMeta;
import com.junbo.common.util.Identifiable;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Created by liangfu on 5/22/14.
 */
public class Organization extends PropertyAssignedAwareResourceMeta implements Identifiable<OrganizationId> {
    @ApiModelProperty(position = 1, required = true, value = "[Nullable]The id of organization resource.")
    @JsonProperty("self")
    private OrganizationId id;

    @ApiModelProperty(position = 2, required = true, value = "The owner of this organization.")
    @JsonProperty("owner")
    private UserId ownerId;

    public OrganizationId getId() {
        return id;
    }

    public void setId(OrganizationId id) {
        this.id = id;
        support.setPropertyAssigned("self");
        support.setPropertyAssigned("id");
    }

    public UserId getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(UserId ownerId) {
        this.ownerId = ownerId;
        support.setPropertyAssigned("owner");
        support.setPropertyAssigned("ownerId");
    }
}
