/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.identity;

import com.junbo.store.spec.model.Challenge;

/**
 * The UserProfileUpdateResponse class.
 */
public class UserProfileUpdateResponse {

    private Challenge challenge;

    private StoreUserProfile userProfile;

    public Challenge getChallenge() {
        return challenge;
    }

    public void setChallenge(Challenge challenge) {
        this.challenge = challenge;
    }

    public StoreUserProfile getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(StoreUserProfile userProfile) {
        this.userProfile = userProfile;
    }
}
