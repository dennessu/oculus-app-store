/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.cloudant.json.annotations.CloudantIgnore;
import com.junbo.common.id.UserCredentialId;
import com.junbo.common.id.UserId;
import com.junbo.common.model.PropertyAssignedAwareResourceMeta;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * Created by liangfu on 4/3/14.
 */
public class UserCredential extends PropertyAssignedAwareResourceMeta<UserCredentialId> {

    @JsonIgnore
    @CloudantIgnore
    private UserCredentialId id;

    @JsonIgnore
    private UserId userId;

    @ApiModelProperty(position = 2, required = false, value = "The current password, plain text.")
    @JsonProperty("oldValue")
    private String currentPassword;

    @ApiModelProperty(position = 3, required = true, value = "The new password/pin, plain text.")
    private String value;

    @ApiModelProperty(position = 4, required = true, value = "Credential type, it must be in [PASSWORD, PIN].")
    private String type;

    @ApiModelProperty(position = 5, required = false, value = "Credential expiration time, must be ISO 8601.")
    private Date expiresBy;

    @ApiModelProperty(position = 6, required = false, value = "Whether user need to change credential next time login.")
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
        support.setPropertyAssigned("userId");
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
        support.setPropertyAssigned("currentPassword");
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
        support.setPropertyAssigned("changeAtNextLogin");
    }

    @Override
    public Integer getResourceAge() {
        // resource Age won't return due to it isn't a resource
        return null;
    }
}
