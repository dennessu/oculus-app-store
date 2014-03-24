/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.resource;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserProfileId;
import com.junbo.common.model.Results;
import com.junbo.identity.rest.service.user.UserProfileService;
import com.junbo.identity.spec.model.common.ResultListUtil;
import com.junbo.identity.spec.model.user.UserProfile;
import com.junbo.identity.spec.resource.UserProfileResource;
import com.junbo.langur.core.promise.Promise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.ext.Provider;
import java.util.List;

/**
 * User profile resource implementation.
 */
@Provider
@Component
@org.springframework.context.annotation.Scope("prototype")
public class UserProfileResourceImpl implements UserProfileResource {
    @Autowired
    private UserProfileService userProfileService;

    @Override
    public Promise<UserProfile> postUserProfile(UserId userId, UserProfile userProfile) {
        return Promise.pure(userProfileService.save(userId.getValue(), userProfile));
    }

    @Override
    public Promise<Results<UserProfile>> getUserProfiles(UserId userId, String type, Integer cursor, Integer count) {
        List<UserProfile> userProfiles = userProfileService.getByUserId(userId.getValue(), type);
        return Promise.pure(ResultListUtil.init(userProfiles, count));
    }

    @Override
    public Promise<UserProfile> getUserProfile(UserId userId, UserProfileId profileId) {
        return Promise.pure(userProfileService.get(userId.getValue(), profileId.getValue()));
    }

    @Override
    public Promise<UserProfile> updateUserProfile(UserId userId, UserProfileId profileId, UserProfile userProfile) {
        return Promise.pure(userProfileService.update(userId.getValue(), profileId.getValue(), userProfile));
    }

    @Override
    public Promise<Void> deleteUserProfile(UserId userId, UserProfileId profileId) {
        userProfileService.deleteProfile(userId.getValue(), profileId.getValue());
        return Promise.pure(null);
    }
}
