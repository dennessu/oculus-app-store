/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.model.users;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.SecurityQuestionId;
import com.junbo.common.id.UserId;
import com.junbo.common.id.UserSecurityQuestionAttemptId;
import com.junbo.common.util.Identifiable;

/**
 * Created by liangfu on 3/25/14.
 */
public class UserSecurityQuestionAttempt extends ResourceMeta implements Identifiable<UserSecurityQuestionAttemptId> {
    @JsonProperty("self")
    private UserSecurityQuestionAttemptId id;

    @JsonProperty("user")
    private UserId userId;

    @JsonProperty("securityQuestion")
    private SecurityQuestionId securityQuestionId;

    private String value;

    private String ipAddress;

    private String userAgent;

    private String clientId;

    // readonly field
    private Boolean succeeded;

    public UserSecurityQuestionAttemptId getId() {
        return id;
    }

    public void setId(UserSecurityQuestionAttemptId id) {
        this.id = id;
    }

    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
    }

    public SecurityQuestionId getSecurityQuestionId() {
        return securityQuestionId;
    }

    public void setSecurityQuestionId(SecurityQuestionId securityQuestionId) {
        this.securityQuestionId = securityQuestionId;
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
