/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.UserId;
import com.junbo.common.id.UserTeleAttemptId;
import com.junbo.common.id.UserTeleId;
import com.junbo.common.util.Identifiable;
import com.junbo.identity.spec.model.users.ResourceMeta;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Created by liangfu on 4/22/14.
 */
public class UserTeleAttempt extends ResourceMeta implements Identifiable<UserTeleAttemptId> {

    @ApiModelProperty(position = 1, required = true,
            value = "[Client Immutable]The id of the userTele attempt resource.")
    @JsonProperty("self")
    private UserTeleAttemptId id;

    @ApiModelProperty(position = 2, required = true, value = "User Tele resource.")
    @JsonProperty("userTele")
    private UserTeleId userTeleId;

    @ApiModelProperty(position = 3, required = true, value = "User resource.")
    @JsonProperty("user")
    private UserId userId;

    @ApiModelProperty(position = 4, required = true, value = "User tele verify code.")
    private String verifyCode;

    @ApiModelProperty(position = 5, required = false, value = "The ip address of the verify attempt caller.")
    private String ipAddress;

    @ApiModelProperty(position = 6, required = false, value = "The user agent of the verify attempt caller.")
    private String userAgent;

    @ApiModelProperty(position = 7, required = false, value = "The client id of the verify attempt caller.")
    private String clientId;

    @ApiModelProperty(position = 8, required = false, value = "[Client Immutable]Whether the attempt is success.")
    private Boolean succeeded;

    public UserTeleAttemptId getId() {
        return id;
    }

    public void setId(UserTeleAttemptId id) {
        this.id = id;
    }

    public UserTeleId getUserTeleId() {
        return userTeleId;
    }

    public void setUserTeleId(UserTeleId userTeleId) {
        this.userTeleId = userTeleId;
    }

    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
    }

    public String getVerifyCode() {
        return verifyCode;
    }

    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public Boolean getSucceeded() {
        return succeeded;
    }

    public void setSucceeded(Boolean succeeded) {
        this.succeeded = succeeded;
    }
}
