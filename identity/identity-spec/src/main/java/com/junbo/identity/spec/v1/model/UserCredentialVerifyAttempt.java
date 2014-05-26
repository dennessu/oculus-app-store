/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.ClientId;
import com.junbo.common.id.UserCredentialVerifyAttemptId;
import com.junbo.common.id.UserId;
import com.junbo.common.model.PropertyAssignedAwareResourceMeta;
import com.junbo.common.util.Identifiable;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Created by liangfu on 4/3/14.
 */
public class UserCredentialVerifyAttempt extends PropertyAssignedAwareResourceMeta implements Identifiable<UserCredentialVerifyAttemptId> {

    @ApiModelProperty(position = 1, required = true,
            value = "[Nullable]The id of user credential attempt resource.")
    @JsonProperty("self")
    private UserCredentialVerifyAttemptId id;

    @ApiModelProperty(position = 2, required = false,
            value = "[Nullable]The user resource for the credential attempt.")
    @JsonProperty("user")
    private UserId userId;

    @ApiModelProperty(position = 3, required = true, value = "User name or email address.")
    private String username;

    @ApiModelProperty(position = 4, required = true,
            value = "The credential require string, must be password or pin in raw format.")
    private String value;

    @ApiModelProperty(position = 5, required = true, value = "The credential type, must be in [PASSWORD, PIN].")
    private String type;

    @ApiModelProperty(position = 6, required = false, value = "The client ip of the verify attempt caller.")
    private String ipAddress;

    @ApiModelProperty(position = 7, required = false, value = "The user agent of the verify attempt caller.")
    private String userAgent;

    @ApiModelProperty(position = 8, required = false, value = "The client id of the verify attempt caller.")
    private ClientId clientId;

    @ApiModelProperty(position = 9, required = false,
            value = "[Nullable]Whether the verify attempt is succeed.")
    @JsonProperty("wasSuccessful")
    private Boolean succeeded;

    public UserCredentialVerifyAttemptId getId() {
        return id;
    }

    public void setId(UserCredentialVerifyAttemptId id) {
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
        support.setPropertyAssigned("username");
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
}
