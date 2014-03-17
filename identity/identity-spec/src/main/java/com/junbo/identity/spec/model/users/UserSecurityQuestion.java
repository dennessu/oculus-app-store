/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.model.users;

import com.junbo.common.id.SecurityQuestionId;
import com.junbo.common.id.UserSecurityQuestionId;
import com.junbo.common.util.Identifiable;

/**
 * Created by kg on 3/10/14.
 */
public class UserSecurityQuestion extends ResourceMeta implements Identifiable<UserSecurityQuestionId> {

    private UserSecurityQuestionId id;

    private SecurityQuestionId securityQuestionId;

    // write only
    private String answer;

    // not read and write possible
    private String answerSalt;
    private String answerHash;

    public UserSecurityQuestionId getId() {
        return id;
    }

    public void setId(UserSecurityQuestionId id) {
        this.id = id;
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

    public String getAnswerSalt() {
        return answerSalt;
    }

    public void setAnswerSalt(String answerSalt) {
        this.answerSalt = answerSalt;
    }

    public String getAnswerHash() {
        return answerHash;
    }

    public void setAnswerHash(String answerHash) {
        this.answerHash = answerHash;
    }
}
