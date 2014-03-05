/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.service.validator.impl;

import com.junbo.identity.data.dao.UserDeviceProfileDAO;
import com.junbo.identity.rest.service.validator.UserDeviceProfileValidator;
import com.junbo.identity.spec.error.AppErrors;
import com.junbo.identity.spec.model.user.UserDeviceProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Created by liangfu on 3/3/14.
 */
@Component
public class UserDeviceProfileValidatorImpl extends CommonValidator implements UserDeviceProfileValidator {
    @Autowired
    private UserDeviceProfileDAO userDeviceProfileDAO;

    @Override
    public void validateCreate(Long userId, UserDeviceProfile userDeviceProfile) {
        if(userId == null || userDeviceProfile == null) {
            throw AppErrors.INSTANCE.invalidNullEmptyInputParam().exception();
        }
        validateNecessaryFields(userId, userDeviceProfile);
        validateUnnecessaryFields(userDeviceProfile);
        if(userDeviceProfile.getResourceAge() != null) {
            throw AppErrors.INSTANCE.unnecessaryParameterField("userDeviceProfile.resourceAge").exception();
        }
        if(userDeviceProfile.getId() != null) {
            throw AppErrors.INSTANCE.unnecessaryParameterField("userDeviceProfile.id").exception();
        }
    }

    @Override
    public void validateUpdate(Long userId, Long deviceProfileId, UserDeviceProfile userDeviceProfile) {
        if(userId == null || deviceProfileId == null || userDeviceProfile == null) {
            throw AppErrors.INSTANCE.invalidNullEmptyInputParam().exception();
        }
        validateNecessaryFields(userId, userDeviceProfile);
        validateUnnecessaryFields(userDeviceProfile);
        if(userDeviceProfile.getResourceAge() == null) {
            throw AppErrors.INSTANCE.missingParameterField("userDeviceProfile.resourceAge").exception();
        }
        if(userDeviceProfile.getId() == null) {
            throw AppErrors.INSTANCE.missingParameterField("userDeviceProfile.id").exception();
        }
    }

    @Override
    public void validateDelete(Long userId, Long deviceProfileId) {
        validateResourceAccessible(userId, deviceProfileId);
    }

    @Override
    public void validateResourceAccessible(Long userId, Long deviceProfileId) {
        checkUserValid(userId);

        UserDeviceProfile userDeviceProfile = userDeviceProfileDAO.get(deviceProfileId);
        if(userDeviceProfile == null) {
            throw AppErrors.INSTANCE.invalidResourceRequest().exception();
        }

        if(!userId.equals(userDeviceProfile.getUserId().getValue())) {
            throw AppErrors.INSTANCE.inputParametersMismatch("userId", "userDeviceProfile.userId").exception();
        }
    }

    private void validateNecessaryFields(Long userId, UserDeviceProfile userDeviceProfile) {
        checkUserValid(userId);
        if(userDeviceProfile.getUserId() == null) {
            throw AppErrors.INSTANCE.missingParameterField("userDeviceProfile.userId").exception();
        }
        if(userDeviceProfile.getDeviceId() == null) {
            throw AppErrors.INSTANCE.missingParameterField("userDeviceProfile.deviceId").exception();
        }
        if(StringUtils.isEmpty(userDeviceProfile.getType())) {
            throw AppErrors.INSTANCE.missingParameterField("userDeviceProfile.type").exception();
        }
        if(userDeviceProfile.getLastUsedDate() == null) {
            throw AppErrors.INSTANCE.missingParameterField("userDeviceProfile.lastUsedDate").exception();
        }
        checkUserDeviceProfileNotExists(userId, userDeviceProfile);
    }

    private void validateUnnecessaryFields(UserDeviceProfile userDeviceProfile) {
        if(userDeviceProfile.getCreatedTime() != null) {
            throw AppErrors.INSTANCE.unnecessaryParameterField("userDeviceProfile.createdTime").exception();
        }
        if(userDeviceProfile.getUpdatedTime() != null) {
            throw AppErrors.INSTANCE.unnecessaryParameterField("userDeviceProfile.updatedTime").exception();
        }
    }

    private void checkUserDeviceProfileNotExists(Long userId, UserDeviceProfile userDeviceProfile) {
        List<UserDeviceProfile> userDeviceProfiles =
                userDeviceProfileDAO.findByUser(userId, userDeviceProfile.getType());

        if(!CollectionUtils.isEmpty(userDeviceProfiles)) {
            for(UserDeviceProfile temp : userDeviceProfiles) {
                if(userDeviceProfile.getDeviceId().getValue().equals(temp.getDeviceId().getValue())) {
                    if(userDeviceProfile.getId() == null
                    || !temp.getId().getValue().equals(userDeviceProfile.getId().getValue())) {
                        throw AppErrors.INSTANCE.userDeviceProfileAlreadyExists(temp.getType(),
                                temp.getDeviceId().getValue().toString()).exception();
                    }
                }
            }
        }
    }
}
