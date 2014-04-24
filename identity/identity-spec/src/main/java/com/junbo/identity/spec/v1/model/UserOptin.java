/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.CommunicationId;
import com.junbo.common.id.UserId;
import com.junbo.common.id.UserOptinId;
import com.junbo.common.util.Identifiable;
import com.junbo.common.model.ResourceMeta;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Created by liangfu on 4/3/14.
 * This won't be supported in v0.5. And it should be first level resource.
 */
public class UserOptin extends ResourceMeta implements Identifiable<UserOptinId> {

    @ApiModelProperty(position = 1, required = true, value = "[Nullable]The id of user optin resource.")
    @JsonProperty("self")
    private UserOptinId id;

    @ApiModelProperty(position = 2, required = true, value = "The user resource.")
    @JsonProperty("user")
    private UserId userId;

    @ApiModelProperty(position = 3, required = true, value = "The communication type.")
    @JsonProperty("communication")
    private CommunicationId communicationId;

    @Override
    public UserOptinId getId() {
        return id;
    }

    public void setId(UserOptinId id) {
        this.id = id;
        support.setPropertyAssigned("self");
        support.setPropertyAssigned("id");
    }

    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
        support.setPropertyAssigned("user");
        support.setPropertyAssigned("userId");
    }

    public CommunicationId getCommunicationId() {
        return communicationId;
    }

    public void setCommunicationId(CommunicationId communicationId) {
        this.communicationId = communicationId;
    }
}
