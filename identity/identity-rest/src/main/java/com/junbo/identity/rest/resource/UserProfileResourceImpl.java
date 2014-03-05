/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.resource;

import com.junbo.identity.rest.service.user.UserProfileService;
import com.junbo.identity.spec.model.common.ResultList;
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
    public Promise<UserProfile> postUserProfile(Long userId, UserProfile userProfile) {
        return Promise.pure(userProfileService.save(userId, userProfile));
    }

    @Override
    public Promise<ResultList<UserProfile>> getUserProfiles(Long userId, String type, Integer cursor, Integer count) {
        List<UserProfile> userProfiles = userProfileService.getByUserId(userId, type);
        return Promise.pure(ResultListUtil.init(userProfiles, count));
    }

    @Override
    public Promise<UserProfile> getUserProfile(Long userId, Long profileId) {
        return Promise.pure(userProfileService.get(userId, profileId));
    }

    @Override
    public Promise<UserProfile> updateUserProfile(Long userId, Long profileId, UserProfile userProfile) {
        return Promise.pure(userProfileService.update(userId, profileId, userProfile));
    }

    @Override
    public Promise<Void> deleteUserProfile(Long userId, Long profileId) {
        userProfileService.deleteProfile(userId, profileId);
        return Promise.pure(null);
    }
}
