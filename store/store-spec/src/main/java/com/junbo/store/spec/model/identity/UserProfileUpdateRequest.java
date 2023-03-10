/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.identity;

import com.junbo.store.spec.model.ChallengeAnswer;

/**
 * The UserProfileUpdateRequest class.
 */
public class UserProfileUpdateRequest {

    private ChallengeAnswer challengeAnswer;

    private StoreUserProfile userProfile;

    public ChallengeAnswer getChallengeAnswer() {
        return challengeAnswer;
    }

    public void setChallengeAnswer(ChallengeAnswer challengeAnswer) {
        this.challengeAnswer = challengeAnswer;
    }

    public StoreUserProfile getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(StoreUserProfile userProfile) {
        this.userProfile = userProfile;
    }
}
