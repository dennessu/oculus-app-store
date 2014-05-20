/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.junbo.common.id.ClientId;
import com.junbo.common.id.UserId;
import com.junbo.common.id.UserSecurityQuestionId;
import com.junbo.common.id.UserSecurityQuestionVerifyAttemptId;
import com.junbo.common.model.PropertyAssignedAwareResourceMeta;
import com.junbo.common.util.Identifiable;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liangfu on 4/3/14.
 */
public class UserSecurityQuestionVerifyAttempt extends PropertyAssignedAwareResourceMeta
        implements Identifiable<UserSecurityQuestionVerifyAttemptId> {

    @ApiModelProperty(position = 1, required = true,
            value = "[Nullable]The id of user security question attempt.")
    @JsonProperty("self")
    private UserSecurityQuestionVerifyAttemptId id;

    @ApiModelProperty(position = 2, required = false, value = "The ip address of the verify attempt caller.")
    private String ipAddress;

    @ApiModelProperty(position = 3, required = false, value = "The user agent of the verify attempt caller.")
    private String userAgent;

    @ApiModelProperty(position = 4, required = false, value = "The client id of the verify attempt caller.")
    private ClientId clientId;

    @ApiModelProperty(position = 5, required = false, value = "[Nullable]Whether the attempt is success.")
    @JsonProperty("wasSuccessful")
    private Boolean succeeded;

    @ApiModelProperty(position = 6, required = true, value = "User security question answer.")
    private String value;

    @ApiModelProperty(position = 7, required = true, value = "User security question resource.")
    @JsonProperty("userSecurityQuestion")
    private UserSecurityQuestionId userSecurityQuestionId;

    @ApiModelProperty(position = 8, required = true, value = "User resource.")
    @JsonProperty("user")
    private UserId userId;

    @ApiModelProperty(position = 9, required = false, value = "The future expansion of userSecurityQuestion resource.")
    private Map<String, JsonNode> futureExpansion = new HashMap<>();

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

    public Map<String, JsonNode> getFutureExpansion() {
        return futureExpansion;
    }

    public void setFutureExpansion(Map<String, JsonNode> futureExpansion) {
        this.futureExpansion = futureExpansion;
        support.setPropertyAssigned("futureExpansion");
    }
}
