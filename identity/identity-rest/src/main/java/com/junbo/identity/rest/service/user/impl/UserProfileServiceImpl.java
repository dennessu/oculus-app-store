/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.service.user.impl;

import com.junbo.identity.data.dao.UserProfileDAO;
import com.junbo.identity.rest.service.user.UserProfileService;
import com.junbo.identity.rest.service.validator.UserProfileValidator;
import com.junbo.identity.spec.model.user.UserProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Transactional
class UserProfileServiceImpl implements UserProfileService {
    @Autowired
    private UserProfileDAO userProfileDAO;

    @Autowired
    private UserProfileValidator validator;

    @Override
    public UserProfile save(Long userId, UserProfile userProfile) {
        validator.validateCreate(userId, userProfile);
        return userProfileDAO.save(userProfile);
    }

    @Override
    public UserProfile update(Long userId, Long profileId, UserProfile userProfile) {
        validator.validateUpdate(userId, profileId, userProfile);
        return userProfileDAO.update(userProfile);
    }

    @Override
    public UserProfile get(Long userId, Long profileId) {
        validator.validateResourceAccessible(userId, profileId);
        return userProfileDAO.get(profileId);
    }

    @Override
    public List<UserProfile> getByUserId(Long userId, String type) {
        return userProfileDAO.findByUser(userId, type);
    }

    @Override
    public void deleteProfile(Long userId, Long profileId) {
        validator.validateDelete(userId, profileId);
        userProfileDAO.delete(profileId);
    }
}
