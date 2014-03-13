/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.SecurityQuestionId;
import com.junbo.common.id.UserId;
import com.junbo.common.id.UserSecurityQuestionId;

/**
 * Created by kg on 3/10/14.
 */
public class UserSecurityQuestion {
    @JsonProperty
    private UserSecurityQuestionId id;

    @JsonProperty("user")
    private UserId userId;

    @JsonProperty("securityQuestion")
    private SecurityQuestionId securityQuestionId;

    private String answer;

    public UserSecurityQuestionId getId() {
        return id;
    }

    public void setId(UserSecurityQuestionId id) {
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

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
