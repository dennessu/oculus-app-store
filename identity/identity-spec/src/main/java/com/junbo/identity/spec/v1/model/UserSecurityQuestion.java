/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.UserId;
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

    @JsonIgnore
    private String answerSalt;

    @JsonIgnore
    private String answerPepper;

    @JsonIgnore
    private String answerHash;

    @JsonIgnore
    private UserId userId;

    @Override
    public UserSecurityQuestionId getId() {
        return id;
    }

    public void setId(UserSecurityQuestionId id) {
        this.id = id;
        support.setPropertyAssigned("self");
        support.setPropertyAssigned("id");
    }

    public String getSecurityQuestion() {
        return securityQuestion;
    }

    public void setSecurityQuestion(String securityQuestion) {
        this.securityQuestion = securityQuestion;
        support.setPropertyAssigned("securityQuestion");
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
        support.setPropertyAssigned("answer");
    }

    public String getAnswerSalt() {
        return answerSalt;
    }

    public void setAnswerSalt(String answerSalt) {
        this.answerSalt = answerSalt;
        support.setPropertyAssigned("answerSalt");
    }

    public String getAnswerPepper() {
        return answerPepper;
    }

    public void setAnswerPepper(String answerPepper) {
        this.answerPepper = answerPepper;
        support.setPropertyAssigned("answerPepper");
    }

    public String getAnswerHash() {
        return answerHash;
    }

    public void setAnswerHash(String answerHash) {
        this.answerHash = answerHash;
        support.setPropertyAssigned("answerHash");
    }

    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
        support.setPropertyAssigned("userId");
    }
}
