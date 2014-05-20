/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.UserId;
import com.junbo.common.id.UserTeleBackupCodeId;
import com.junbo.common.model.PropertyAssignedAwareResourceMeta;
import com.junbo.common.util.Identifiable;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * Created by liangfu on 4/22/14.
 */
public class UserTeleBackupCode extends PropertyAssignedAwareResourceMeta implements Identifiable<UserTeleBackupCodeId> {
    @ApiModelProperty(position = 1, required = true, value = "[Client Immutable]The id of UserTeleCode resource.")
    @JsonProperty("self")
    private UserTeleBackupCodeId id;

    @ApiModelProperty(position = 2, required = true, value = "[Client Immutable]The id of user resource.")
    @JsonProperty("user")
    private UserId userId;

    @ApiModelProperty(position = 3, required = true, value = "[Client Immutable]The verify code generated..")
    private String verifyCode;

    @ApiModelProperty(position = 4, required = true, value = "The verify expires time.")
    private Date expiresBy;

    @ApiModelProperty(position = 5, required = false, value = "[Client Immutable]Whether user Tele resource is active.")
    private Boolean active;

    public UserTeleBackupCodeId getId() {
        return id;
    }

    public void setId(UserTeleBackupCodeId id) {
        this.id = id;
        support.setPropertyAssigned("id");
        support.setPropertyAssigned("self");
    }

    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
        support.setPropertyAssigned("userId");
        support.setPropertyAssigned("user");
    }

    public String getVerifyCode() {
        return verifyCode;
    }

    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
        support.setPropertyAssigned("verifyCode");
    }

    public Date getExpiresBy() {
        return expiresBy;
    }

    public void setExpiresBy(Date expiresBy) {
        this.expiresBy = expiresBy;
        support.setPropertyAssigned("expiresBy");
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
        support.setPropertyAssigned("active");
    }
}
