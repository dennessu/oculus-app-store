/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.identity.spec.v1.model.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.UserEmailId;
import com.junbo.common.id.UserId;

/**
 * Created by kg on 3/10/14.
 */
public class UserEmail {

    @JsonProperty("user")
    private UserId userId;

    @JsonProperty("self")
    private UserEmailId userEmailId;

    private String value;

    private String type;

    private Boolean primary;

    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
    }

    public UserEmailId getUserEmailId() {
        return userEmailId;
    }

    public void setUserEmailId(UserEmailId userEmailId) {
        this.userEmailId = userEmailId;
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

    public Boolean getPrimary() {
        return primary;
    }

    public void setPrimary(Boolean primary) {
        this.primary = primary;
    }
}
