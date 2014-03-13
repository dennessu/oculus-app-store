/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.identity.spec.v1.model.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.UserId;

/**
 * Created by kg on 3/10/14.
 */
public class UserExternalId {

    @JsonProperty("user")
    private UserId userId;

    @JsonProperty("self")
    private UserExternalId userExternalId;

    private String value;

    private String type;

    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
    }

    public UserExternalId getUserExternalId() {
        return userExternalId;
    }

    public void setUserExternalId(UserExternalId userExternalId) {
        this.userExternalId = userExternalId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
