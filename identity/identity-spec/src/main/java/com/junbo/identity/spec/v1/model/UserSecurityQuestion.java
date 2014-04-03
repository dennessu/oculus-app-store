/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.UserSecurityQuestionId;
import com.junbo.common.util.Identifiable;
import com.junbo.identity.spec.model.users.ResourceMeta;

/**
 * Created by liangfu on 4/3/14.
 */
public class UserSecurityQuestion extends ResourceMeta implements Identifiable<UserSecurityQuestionId> {

    @JsonProperty("self")
    private UserSecurityQuestionId id;

    private String securityQuestion;

    // This is the write only field
    private String answer;

    @Override
    public UserSecurityQuestionId getId() {
        return id;
    }

    public void setId(UserSecurityQuestionId id) {
        this.id = id;
    }

    public String getSecurityQuestion() {
        return securityQuestion;
    }

    public void setSecurityQuestion(String securityQuestion) {
        this.securityQuestion = securityQuestion;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
