/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.core.service.validator.impl;

import com.junbo.common.id.UserDeviceId;
import com.junbo.common.id.UserId;
import com.junbo.identity.data.repository.UserDeviceRepository;
import com.junbo.identity.core.service.validator.UserDeviceValidator;
import com.junbo.identity.spec.error.AppErrors;
import com.junbo.identity.spec.model.users.UserDevice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Created by liangfu on 3/3/14.
 */
@Component
public class UserDeviceValidatorImpl extends CommonValidator implements UserDeviceValidator {
    @Autowired
    private UserDeviceRepository userDeviceRepository;

    @Override
    public void validateCreate(UserId userId, UserDevice userDeviceProfile) {
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
    public void validateUpdate(UserId userId, UserDeviceId userDeviceId, UserDevice userDevice) {
        if(userId == null || userDeviceId == null || userDevice == null) {
            throw AppErrors.INSTANCE.invalidNullEmptyInputParam().exception();
        }
        validateNecessaryFields(userId, userDevice);
        validateUnnecessaryFields(userDevice);
        if(userDevice.getResourceAge() == null) {
            throw AppErrors.INSTANCE.missingParameterField("userDeviceProfile.resourceAge").exception();
        }
        if(userDevice.getId() == null) {
            throw AppErrors.INSTANCE.missingParameterField("userDeviceProfile.id").exception();
        }
    }

    @Override
    public void validateDelete(UserId userId, UserDeviceId userDeviceId) {
        validateResourceAccessible(userId, userDeviceId);
    }

    @Override
    public void validateResourceAccessible(UserId userId, UserDeviceId userDeviceId) {
        checkUserValid(userId);

        UserDevice userDeviceProfile = userDeviceRepository.get(userDeviceId);
        if(userDeviceProfile == null) {
            throw AppErrors.INSTANCE.invalidResourceRequest().exception();
        }
    }

    private void validateNecessaryFields(UserId userId, UserDevice userDeviceProfile) {
        checkUserValid(userId);

        if(userDeviceProfile.getDeviceId() == null) {
            throw AppErrors.INSTANCE.missingParameterField("userDeviceProfile.deviceId").exception();
        }
        if(StringUtils.isEmpty(userDeviceProfile.getType())) {
            throw AppErrors.INSTANCE.missingParameterField("userDeviceProfile.type").exception();
        }
        checkUserDeviceProfileNotExists(userId, userDeviceProfile);
    }

    private void validateUnnecessaryFields(UserDevice userDeviceProfile) {
        if(userDeviceProfile.getCreatedTime() != null) {
            throw AppErrors.INSTANCE.unnecessaryParameterField("userDeviceProfile.createdTime").exception();
        }
        if(userDeviceProfile.getUpdatedTime() != null) {
            throw AppErrors.INSTANCE.unnecessaryParameterField("userDeviceProfile.updatedTime").exception();
        }
    }

    private void checkUserDeviceProfileNotExists(UserId userId, UserDevice userDeviceProfile) {

    }
}
