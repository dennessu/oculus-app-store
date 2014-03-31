/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.model.users;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.SecurityQuestionId;
import com.junbo.common.id.UserId;
import com.junbo.common.id.UserSecurityQuestionId;
import com.junbo.common.util.Identifiable;

/**
 * Created by kg on 3/10/14.
 */
public class UserSecurityQuestion extends ResourceMeta implements Identifiable<UserSecurityQuestionId> {

    @JsonProperty("self")
    private UserSecurityQuestionId id;

    @JsonProperty("securityQuestion")
    private SecurityQuestionId securityQuestionId;

    // write only
    private String answer;

    // not read and write possible
    private Boolean active;

    @JsonIgnore
    private String answerSalt;
    @JsonIgnore
    private String answerHash;
    @JsonIgnore
    private UserId userId;

    public UserSecurityQuestionId getId() {
        return id;
    }

    public void setId(UserSecurityQuestionId id) {
        this.id = id;
        support.setPropertyAssigned("id");
        support.setPropertyAssigned("self");
    }

    public SecurityQuestionId getSecurityQuestionId() {
        return securityQuestionId;
    }

    public void setSecurityQuestionId(SecurityQuestionId securityQuestionId) {
        this.securityQuestionId = securityQuestionId;
        support.setPropertyAssigned("securityQuestionId");
        support.setPropertyAssigned("securityQuestion");
    }

    public String getAnswer() {
        return answer;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
        support.setPropertyAssigned("active");
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
