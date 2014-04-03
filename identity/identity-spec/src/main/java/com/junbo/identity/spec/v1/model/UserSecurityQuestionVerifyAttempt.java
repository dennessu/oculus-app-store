/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.UserId;
import com.junbo.common.id.UserSecurityQuestionVerifyAttemptId;
import com.junbo.common.id.UserSecurityQuestionId;
import com.junbo.common.util.Identifiable;
import com.junbo.identity.spec.model.users.ResourceMeta;

/**
 * Created by liangfu on 4/3/14.
 */
public class UserSecurityQuestionVerifyAttempt extends ResourceMeta
        implements Identifiable<UserSecurityQuestionVerifyAttemptId> {

    @JsonIgnore
    private UserSecurityQuestionVerifyAttemptId id;

    @JsonProperty("securityQuestion")
    private UserSecurityQuestionId userSecurityQuestionId;

    @JsonProperty("user")
    private UserId userId;

    private String value;

    private String ipAddress;

    private String userAgent;

    private String clientId;

    // read only
    private Boolean succeeded;

    public UserSecurityQuestionVerifyAttemptId getId() {
        return id;
    }

    public void setId(UserSecurityQuestionVerifyAttemptId id) {
        this.id = id;
    }

    public UserSecurityQuestionId getUserSecurityQuestionId() {
        return userSecurityQuestionId;
    }

    public void setUserSecurityQuestionId(UserSecurityQuestionId userSecurityQuestionId) {
        this.userSecurityQuestionId = userSecurityQuestionId;
    }

    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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
