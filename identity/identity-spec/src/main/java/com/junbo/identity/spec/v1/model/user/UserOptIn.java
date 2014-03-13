/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.UserId;
import com.junbo.common.id.UserOptInId;

/**
 * Created by liangfu on 3/13/14.
 */
public class UserOptIn {
    @JsonProperty("user")
    private UserId userId;

    @JsonProperty("self")
    private UserOptInId id;

    private String optIn;

    private Boolean active;

    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
    }

    public UserOptInId getId() {
        return id;
    }

    public void setId(UserOptInId id) {
        this.id = id;
    }

    public String getOptIn() {
        return optIn;
    }

    public void setOptIn(String optIn) {
        this.optIn = optIn;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
