/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.identity;

import com.junbo.store.spec.model.BaseResponse;

/**
 * The UserProfileGetResponse class.
 */
public class UserProfileGetResponse extends BaseResponse {

    StoreUserProfile userProfile;

    public StoreUserProfile getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(StoreUserProfile userProfile) {
        this.userProfile = userProfile;
    }
}
