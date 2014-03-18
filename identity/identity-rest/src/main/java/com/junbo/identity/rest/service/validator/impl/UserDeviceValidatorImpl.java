/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.service.validator.impl;

import com.junbo.common.id.UserDeviceId;
import com.junbo.identity.data.repository.UserDeviceRepository;
import com.junbo.identity.rest.service.validator.UserDeviceValidator;
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
    public void validateCreate(Long userId, UserDevice userDeviceProfile) {
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
    public void validateUpdate(Long userId, Long deviceProfileId, UserDevice userDeviceProfile) {
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

        UserDevice userDeviceProfile = userDeviceRepository.get(new UserDeviceId(deviceProfileId));
        if(userDeviceProfile == null) {
            throw AppErrors.INSTANCE.invalidResourceRequest().exception();
        }
    }

    private void validateNecessaryFields(Long userId, UserDevice userDeviceProfile) {
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

    private void checkUserDeviceProfileNotExists(Long userId, UserDevice userDeviceProfile) {

    }
}
