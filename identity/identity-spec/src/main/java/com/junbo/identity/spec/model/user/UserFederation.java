/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.model.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.UserFederationId;
import com.junbo.common.id.UserId;
import com.junbo.identity.spec.model.common.CommonStamp;

/**
 * Java cod for UserFederation.
 */
public class UserFederation  extends CommonStamp {
    @JsonProperty("self")
    private UserFederationId id;

    @JsonProperty("user")
    private UserId userId;

    private String type;
    private String value;

    public UserFederationId getId() {
        return id;
    }

    public void setId(UserFederationId id) {
        this.id = id;
    }

    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
