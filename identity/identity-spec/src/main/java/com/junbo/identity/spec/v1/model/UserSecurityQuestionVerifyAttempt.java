/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.ClientId;
import com.junbo.common.id.UserId;
import com.junbo.common.id.UserSecurityQuestionId;
import com.junbo.common.id.UserSecurityQuestionVerifyAttemptId;
import com.junbo.common.model.PropertyAssignedAwareResourceMeta;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Created by liangfu on 4/3/14.
 */
public class UserSecurityQuestionVerifyAttempt extends PropertyAssignedAwareResourceMeta<UserSecurityQuestionVerifyAttemptId> {

    @ApiModelProperty(position = 1, required = true,
            value = "[Client Immutable] Link to the user security question attempt.")
    @JsonProperty("self")
    private UserSecurityQuestionVerifyAttemptId id;

    @ApiModelProperty(position = 2, required = false, value = "[Nullable]The ip address where the security question attempt is initiated.")
    private String ipAddress;

    @ApiModelProperty(position = 3, required = false, value = "[Nullable]The agent where the UserSecurityQuestionVerifyAttempt is passed through. " +
            "For example, if user do UserSecurityQuestionVerifyAttempt via a webkit from game \"Angry Bird\", " +
            "the userAgent will be the Webkit(FireFox or Chrome, etc).")
    private String userAgent;

    @ApiModelProperty(position = 4, required = false, value = "The OAuth client ID for the component where the UserTeleAttempt is initiated. " +
            "For example, if user do theUserTeleAttempt via a webkit from game \"Angry Bird\", " +
            "the clientId will be the clientId string for the game \"Angry Bird\". " +
            "The clientId is a string developer get from Oculus platform and embed into the game binary. " +
            "It then get embedded to the game binary, and get passed everytime game binary call into Oculus API to identify the game.")
    private ClientId clientId;

    @ApiModelProperty(position = 5, required = false, value = "[Client Immutable]Whether the attempt was success.")
    @JsonProperty("wasSuccessful")
    private Boolean succeeded;

    @ApiModelProperty(position = 6, required = true, value = "User security question answer.")
    private String value;

    @ApiModelProperty(position = 7, required = true, value = "Link to User security question resource.")
    @JsonProperty("userSecurityQuestion")
    private UserSecurityQuestionId userSecurityQuestionId;

    @ApiModelProperty(position = 8, required = true, value = "User who this security question verify attempt is for.")
    @JsonProperty("user")
    private UserId userId;

    public UserSecurityQuestionVerifyAttemptId getId() {
        return id;
    }

    public void setId(UserSecurityQuestionVerifyAttemptId id) {
        this.id = id;
        support.setPropertyAssigned("id");
        support.setPropertyAssigned("self");
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
        support.setPropertyAssigned("wasSuccessful");
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
        support.setPropertyAssigned("value");
    }

    public UserSecurityQuestionId getUserSecurityQuestionId() {
        return userSecurityQuestionId;
    }

    public void setUserSecurityQuestionId(UserSecurityQuestionId userSecurityQuestionId) {
        this.userSecurityQuestionId = userSecurityQuestionId;
        support.setPropertyAssigned("userSecurityQuestionId");
        support.setPropertyAssigned("userSecurityQuestion");
    }

    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
        support.setPropertyAssigned("userId");
        support.setPropertyAssigned("user");
    }
}
