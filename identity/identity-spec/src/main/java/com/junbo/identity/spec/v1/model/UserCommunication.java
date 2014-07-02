/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.CommunicationId;
import com.junbo.common.id.UserCommunicationId;
import com.junbo.common.id.UserId;
import com.junbo.common.model.PropertyAssignedAwareResourceMeta;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Created by liangfu on 4/3/14.
 */
public class UserCommunication extends PropertyAssignedAwareResourceMeta<UserCommunicationId> {

    @ApiModelProperty(position = 1, required = true, value = "[Client Immutable]Link to this user optin resource, " +
            "OptIn represents a link between a user and a communication, it means user decides to subscribe to the communication.")
    @JsonProperty("self")
    private UserCommunicationId id;

    @ApiModelProperty(position = 2, required = true, value = "Link to the user, who decides to optin the communication.")
    @JsonProperty("user")
    private UserId userId;

    @ApiModelProperty(position = 3, required = true, value = "Link to the communication, which user subscribes to.")
    @JsonProperty("communication")
    private CommunicationId communicationId;

    @Override
    public UserCommunicationId getId() {
        return id;
    }

    public void setId(UserCommunicationId id) {
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
        support.setPropertyAssigned("communication");
        support.setPropertyAssigned("communicationId");
    }
}
