/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.model.users;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.UserId;
import com.junbo.common.id.UserTosId;
import com.junbo.common.util.Identifiable;

/**
 * Created by kg on 3/10/14.
 */
public class UserTos extends ResourceMeta implements Identifiable<UserTosId> {

    @JsonProperty("self")
    private UserTosId id;

    private String tosUri;

    // not readable or writable
    @JsonIgnore
    private UserId userId;

    public UserTosId getId() {
        return id;
    }

    public void setId(UserTosId id) {
        this.id = id;
        support.setPropertyAssigned("id");
        support.setPropertyAssigned("self");
    }

    public String getTosUri() {
        return tosUri;
    }

    public void setTosUri(String tosUri) {
        this.tosUri = tosUri;
        support.setPropertyAssigned("tosUri");
    }

    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
        support.setPropertyAssigned("userId");
    }
}
