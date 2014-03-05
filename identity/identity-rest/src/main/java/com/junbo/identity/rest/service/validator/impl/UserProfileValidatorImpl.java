/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.service.validator.impl;

import com.junbo.identity.data.dao.UserProfileDAO;
import com.junbo.identity.rest.service.validator.UserProfileValidator;
import com.junbo.identity.spec.error.AppErrors;
import com.junbo.identity.spec.model.user.UserProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Created by liangfu on 2/28/14.
 */
@Component
public class UserProfileValidatorImpl extends CommonValidator implements UserProfileValidator {
    @Autowired
    private UserProfileDAO userProfileDAO;

    @Override
    public void validateCreate(Long userId, UserProfile userProfile) {
        if(userId == null || userProfile == null) {
            throw AppErrors.INSTANCE.invalidNullEmptyInputParam().exception();
        }
        validateNecessaryFields(userId, userProfile);
        validateUnnecessaryFields(userProfile);
        if(userProfile.getResourceAge() != null) {
            throw AppErrors.INSTANCE.unnecessaryParameterField("userProfile.resourceAge").exception();
        }
        if(userProfile.getId() != null) {
            throw AppErrors.INSTANCE.unnecessaryParameterField("userProfile.id").exception();
        }
    }

    @Override
    public void validateUpdate(Long userId, Long profileId, UserProfile userProfile) {
        if(userId == null || profileId == null || userProfile == null) {
            throw AppErrors.INSTANCE.invalidNullEmptyInputParam().exception();
        }
        validateNecessaryFields(userId, userProfile);
        validateUnnecessaryFields(userProfile);
        validateResourceAccessible(userId, profileId);
        if(userProfile.getResourceAge() == null) {
            throw AppErrors.INSTANCE.missingParameterField("userProfile.resourceAge").exception();
        }
        if(userProfile.getId() == null) {
            throw AppErrors.INSTANCE.missingParameterField("userProfile.id").exception();
        }
    }

    @Override
    public void validateDelete(Long userId, Long profileId) {
        validateResourceAccessible(userId, profileId);
    }

    @Override
    public void validateResourceAccessible(Long userId, Long profileId) {
        checkUserValid(userId);

        UserProfile userProfile = userProfileDAO.get(profileId);
        if(userProfile == null) {
            throw AppErrors.INSTANCE.invalidResourceRequest().exception();
        }

        if(!userId.equals(userProfile.getUserId().getValue())) {
            throw AppErrors.INSTANCE.inputParametersMismatch("userId", "userProfileId").exception();
        }
    }

    private void validateNecessaryFields(Long userId, UserProfile userProfile) {
        checkUserValid(userId);
        if(userProfile.getUserId() == null) {
            throw AppErrors.INSTANCE.missingParameterField("userProfile.userId").exception();
        }
        if(StringUtils.isEmpty(userProfile.getType())) {
            throw AppErrors.INSTANCE.missingParameterField("userProfile.type").exception();
        }
        checkUserProfileNotExists(userId, userProfile);
    }

    private void validateUnnecessaryFields(UserProfile userProfile) {
        if(userProfile.getCreatedTime() != null) {
            throw AppErrors.INSTANCE.unnecessaryParameterField("userProfile.createdTime").exception();
        }
        if(userProfile.getUpdatedTime() != null) {
            throw AppErrors.INSTANCE.unnecessaryParameterField("userProfile.updatedTime").exception();
        }
    }

    private void checkUserProfileNotExists(Long userId, UserProfile userProfile) {
        List<UserProfile> userProfiles = userProfileDAO.findByUser(userId, userProfile.getType());

        if(!CollectionUtils.isEmpty(userProfiles)) {
            for(UserProfile profile : userProfiles) {
                if(userProfile.getId() == null || !profile.getId().getValue().equals(userProfile.getId().getValue())) {
                    throw AppErrors.INSTANCE.userProfileAlreadyExists(userProfile.getType()).exception();
                }
            }
        }
    }
}
