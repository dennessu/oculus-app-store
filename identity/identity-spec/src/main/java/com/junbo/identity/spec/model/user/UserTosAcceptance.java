/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.model.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.UserId;
import com.junbo.common.id.UserTosId;
import com.junbo.identity.spec.model.common.CommonStamp;
import java.util.Date;

/**
 * Java cod for UserTosAcceptance.
 */
public class UserTosAcceptance extends CommonStamp{
    @JsonProperty("self")
    private UserTosId id;

    @JsonProperty("user")
    private UserId userId;
    private String tos;
    private Date dateAccepted;

    public UserTosId getId() {
        return id;
    }

    public void setId(UserTosId id) {
        this.id = id;
    }

    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
    }

    public String getTos() {
        return tos;
    }

    public void setTos(String tos) {
        this.tos = tos;
    }

    public Date getDateAccepted() {
        return dateAccepted;
    }

    public void setDateAccepted(Date dateAccepted) {
        this.dateAccepted = dateAccepted;
    }
}
