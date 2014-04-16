/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.UserCredentialVerifyAttemptId;
import com.junbo.common.id.UserId;
import com.junbo.common.util.Identifiable;
import com.junbo.identity.spec.model.users.ResourceMeta;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Created by liangfu on 4/3/14.
 */
public class UserCredentialVerifyAttempt extends ResourceMeta implements Identifiable<UserCredentialVerifyAttemptId> {

    // Only support search, post. No Get method is supported.
    @JsonIgnore
    private UserCredentialVerifyAttemptId id;

    @ApiModelProperty(position = 1, required = false, value = "The user resource for the credential attempt.")
    @JsonProperty("user")
    private UserId userId;

    @ApiModelProperty(position = 2, required = true, value = "The credential type, must be password or pin.")
    private String type;

    // base 64
    @ApiModelProperty(position = 3, required = true,
            value = "The credential require string, must be base64 encode of username:password")
    private String value;

    @ApiModelProperty(position = 4, required = true, value = "The client id of the verify attempt caller.")
    private String ipAddress;

    @ApiModelProperty(position = 5, required = true, value = "The user agent of the verify attempt caller.")
    private String userAgent;

    @ApiModelProperty(position = 6, required = true, value = "The client id of the verify attempt caller.")
    private String clientId;

    // read only
    @ApiModelProperty(position = 7, required = false, value = "Whether the verify attempt is succeed.")
    private Boolean succeeded;

    public UserCredentialVerifyAttemptId getId() {
        return id;
    }

    public void setId(UserCredentialVerifyAttemptId id) {
        this.id = id;
        support.setPropertyAssigned("id");
    }

    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
        support.setPropertyAssigned("userId");
        support.setPropertyAssigned("user");
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
        support.setPropertyAssigned("type");
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
        support.setPropertyAssigned("value");
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

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
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
