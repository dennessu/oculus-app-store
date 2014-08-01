/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.UserAuthenticatorId;
import com.junbo.common.id.UserId;
import com.junbo.common.jackson.annotation.XSSFreeString;
import com.junbo.common.model.PropertyAssignedAwareResourceMeta;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Created by liangfu on 4/3/14.
 */
public class UserAuthenticator extends PropertyAssignedAwareResourceMeta<UserAuthenticatorId> {

    @ApiModelProperty(position = 1, required = true, value = "[Client Immutable]The Link to the Authenticator resource.")
    @JsonProperty("self")
    private UserAuthenticatorId id;

    @ApiModelProperty(position = 2, required = true, value = "Link to the User resource that owns this particular Authenticator.")
    @JsonProperty("user")
    private UserId userId;

    @ApiModelProperty(position = 3, required = true,
            value = "External authenticator type, must be in [GOOGLE, FACEBOOK, TWITTER].")
    private String type;

    @XSSFreeString
    @ApiModelProperty(position = 4, required = true, value = "The user ID that is used by the user to authenticate with the external authenticator.")
    @JsonProperty("externalUserId")
    private String externalId;

    @Override
    public UserAuthenticatorId getId() {
        return id;
    }

    public void setId(UserAuthenticatorId id) {
        this.id = id;
        support.setPropertyAssigned("id");
        support.setPropertyAssigned("self");
    }

    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
        support.setPropertyAssigned("userId");
        support.setPropertyAssigned("user");
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
        support.setPropertyAssigned("type");
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
        support.setPropertyAssigned("externalId");
        support.setPropertyAssigned("externalUserId");
    }
}
