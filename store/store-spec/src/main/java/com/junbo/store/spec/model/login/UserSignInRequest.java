/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.login;

import com.junbo.store.spec.model.ChallengeAnswer;

/**
 * The UserSignInRequest class.
 */
public class UserSignInRequest {

    private ChallengeAnswer challengeAnswer;

    private String email;

    private UserCredential userCredential;

    public ChallengeAnswer getChallengeAnswer() {
        return challengeAnswer;
    }

    public void setChallengeAnswer(ChallengeAnswer challengeAnswer) {
        this.challengeAnswer = challengeAnswer;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserCredential getUserCredential() {
        return userCredential;
    }

    public void setUserCredential(UserCredential userCredential) {
        this.userCredential = userCredential;
    }
}
