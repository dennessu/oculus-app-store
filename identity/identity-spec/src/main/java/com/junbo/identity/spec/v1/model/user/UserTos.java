/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.UserId;
import com.junbo.common.id.UserTosAcceptanceId;

import java.util.Date;

/**
 * Created by kg on 3/10/14.
 */
public class UserTos {

    @JsonProperty("user")
    private UserId userId;

    @JsonProperty("self")
    private UserTosAcceptanceId id;

    private String tosUri;

    private Date dateAccepted;

    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
    }

    public UserTosAcceptanceId getId() {
        return id;
    }

    public void setId(UserTosAcceptanceId id) {
        this.id = id;
    }

    public String getTosUri() {
        return tosUri;
    }

    public void setTosUri(String tosUri) {
        this.tosUri = tosUri;
    }

    public Date getDateAccepted() {
        return dateAccepted;
    }

    public void setDateAccepted(Date dateAccepted) {
        this.dateAccepted = dateAccepted;
    }
}
