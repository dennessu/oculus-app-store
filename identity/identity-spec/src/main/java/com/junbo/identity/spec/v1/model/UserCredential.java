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
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * Created by liangfu on 4/3/14.
 */
public class UserCredential extends ResourceMeta implements Identifiable<UserCredentialId> {

    @JsonIgnore
    private UserCredentialId id;

    @ApiModelProperty(position = 1, required = true, value = "The user resource for the credential.")
    @JsonProperty("user")
    private UserId userId;

    @ApiModelProperty(position = 2, required = false, value = "The old password, it must be base64 encoded.")
    private String oldValue;

    @ApiModelProperty(position = 3, required = true, value = "The new password, it must be base64 encoded.")
    private String value;

    @ApiModelProperty(position = 4, required = true, value = "Credential type, it must be password or pin.")
    private String type;

    @ApiModelProperty(position = 5, required = false, value = "Credential expire time.")
    private Date expiresBy;

    @ApiModelProperty(position = 6, required = false, value = "Whether need to change credential next login.")
    private Boolean changeAtNextLogin;

    @Override
    public UserCredentialId getId() {
        return id;
    }

    public void setId(UserCredentialId id) {
        this.id = id;
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

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
        support.setPropertyAssigned("oldValue");
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
        support.setPropertyAssigned("value");
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
        support.setPropertyAssigned("type");
    }

    public Date getExpiresBy() {
        return expiresBy;
    }

    public void setExpiresBy(Date expiresBy) {
        this.expiresBy = expiresBy;
        support.setPropertyAssigned("expiresBy");
    }

    public Boolean getChangeAtNextLogin() {
        return changeAtNextLogin;
    }

    public void setChangeAtNextLogin(Boolean changeAtNextLogin) {
        this.changeAtNextLogin = changeAtNextLogin;
        support.setPropertyAssigned("expiresBy");
    }
}
