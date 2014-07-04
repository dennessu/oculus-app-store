/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.ClientId;
import com.junbo.common.id.UserId;
import com.junbo.common.id.UserTFAAttemptId;
import com.junbo.common.id.UserTFAId;
import com.junbo.common.model.PropertyAssignedAwareResourceMeta;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Created by liangfu on 4/22/14.
 */
public class UserTFAAttempt extends PropertyAssignedAwareResourceMeta<UserTFAAttemptId> {

    @ApiModelProperty(position = 1, required = true,
            value = "[Client Immutable]Link to the userTfaAttempt resource.")
    @JsonProperty("self")
    private UserTFAAttemptId id;

    @ApiModelProperty(position = 2, required = true, value = "Link to the UserTfa resource that this userTfaAttempt associates to.")
    @JsonProperty("userTFA")
    private UserTFAId userTFAId;

    @ApiModelProperty(position = 3, required = true, value = "User who initiate the UserTfaAttempt.")
    @JsonProperty("user")
    private UserId userId;

    @ApiModelProperty(position = 4, required = true, value = "The code generated for TFA.")
    private String verifyCode;

    @ApiModelProperty(position = 5, required = false, value = "[Nullable]The ip address where the TFA request is initiated.")
    private String ipAddress;

    @ApiModelProperty(position = 6, required = false, value = "[Nullable]The agent where the UserTfaAttempt is passed through. " +
            "For example, if user do the UserTfaAttempt via a webkit from game \"Angry Bird\", the userAgent will be the Webkit(FireFox or Chrome, etc).")
    private String userAgent;

    @ApiModelProperty(position = 7, required = false, value = "The OAuth client ID for the component where the UserTfaAttempt is initiated. " +
            "For example, if user do the UserTfaAttempt via a webkit from game \"Angry Bird\", " +
            "the clientId will be the clientId string for the game \"Angry Bird\". " +
            "The clientId is a string developer get from Oculus platform and embed into the game binary. " +
            "It then get embedded to the game binary, and get passed everytime game binary call into Oculus API to identify the game.")
    private ClientId clientId;

    @ApiModelProperty(position = 8, required = false, value = "[Client Immutable]Whether the attempt was success.")
    private Boolean succeeded;

    public void setId(UserTFAAttemptId id) {
        this.id = id;
        support.setPropertyAssigned("id");
        support.setPropertyAssigned("self");
    }

    public UserTFAAttemptId getId() {
        return id;
    }

    public UserTFAId getUserTFAId() {
        return userTFAId;
    }

    public void setUserTFAId(UserTFAId userTFAId) {
        this.userTFAId = userTFAId;
        support.setPropertyAssigned("userTFAId");
        support.setPropertyAssigned("userTFA");
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

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
        support.setPropertyAssigned("ipAddress");
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
        support.setPropertyAssigned("userAgent");
    }

    public ClientId getClientId() {
        return clientId;
    }

    public void setClientId(ClientId clientId) {
        this.clientId = clientId;
        support.setPropertyAssigned("clientId");
    }

    public Boolean getSucceeded() {
        return succeeded;
    }

    public void setSucceeded(Boolean succeeded) {
        this.succeeded = succeeded;
        support.setPropertyAssigned("succeeded");
    }
}
