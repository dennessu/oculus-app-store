/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.UserCredentialId;
import com.junbo.common.id.UserId;
import com.junbo.common.util.Identifiable;
import com.junbo.identity.spec.model.users.ResourceMeta;

import java.util.Date;

/**
 * Created by liangfu on 4/3/14.
 */
public class UserCredential extends ResourceMeta implements Identifiable<UserCredentialId> {

    @JsonIgnore
    private UserCredentialId id;

    @JsonProperty("user")
    private UserId userId;

    private String oldValue;

    private String value;

    private String type;

    private Date expiresBy;

    private Boolean changeAtNextLogin;

    @Override
    public UserCredentialId getId() {
        return id;
    }

    public void setId(UserCredentialId id) {
        this.id = id;
    }

    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
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

    public Date getExpiresBy() {
        return expiresBy;
    }

    public void setExpiresBy(Date expiresBy) {
        this.expiresBy = expiresBy;
    }

    public Boolean getChangeAtNextLogin() {
        return changeAtNextLogin;
    }

    public void setChangeAtNextLogin(Boolean changeAtNextLogin) {
        this.changeAtNextLogin = changeAtNextLogin;
    }
}
