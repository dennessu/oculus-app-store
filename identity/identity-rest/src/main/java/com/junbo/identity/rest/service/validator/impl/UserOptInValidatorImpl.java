/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.service.validator.impl;

import com.junbo.common.id.UserOptinId;
import com.junbo.identity.data.dao.UserOptinDAO;
import com.junbo.identity.rest.service.validator.UserOptinValidator;
import com.junbo.identity.spec.error.AppErrors;
import com.junbo.identity.spec.model.users.UserOptin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by liangfu on 2/28/14.
 */
@Component
public class UserOptinValidatorImpl extends CommonValidator implements UserOptinValidator {
    @Autowired
    private UserOptinDAO userOptInDAO;

    @Override
    public void validateCreate(Long userId, UserOptin userOptIn) {
        if(userId == null || userOptIn == null) {
            throw AppErrors.INSTANCE.invalidNullEmptyInputParam().exception();
        }
        validateNecessaryFields(userId, userOptIn);
        validateUnnecessaryFields(userOptIn);
        if(userOptIn.getResourceAge() != null) {
            throw AppErrors.INSTANCE.unnecessaryParameterField("userOptIn.resourceAge").exception();
        }
        if(userOptIn.getId() != null) {
            throw AppErrors.INSTANCE.unnecessaryParameterField("userOptIn.id").exception();
        }
    }

    @Override
    public void validateUpdate(Long userId, Long optInId, UserOptin userOptIn) {
        if(userId == null || optInId == null || userOptIn == null) {
            throw AppErrors.INSTANCE.invalidNullEmptyInputParam().exception();
        }
        validateNecessaryFields(userId, userOptIn);
        validateUnnecessaryFields(userOptIn);
        if(userOptIn.getResourceAge() == null) {
            throw AppErrors.INSTANCE.missingParameterField("userOptIn.resourceAge").exception();
        }
        if(userOptIn.getId() == null) {
            throw AppErrors.INSTANCE.missingParameterField("userOptIn.id").exception();
        }
    }

    @Override
    public void validateDelete(Long userId, Long optInId) {
        validateResourceAccessible(userId, optInId);
    }

    @Override
    public void validateResourceAccessible(Long userId, Long optInId) {
        checkUserValid(userId);

        UserOptin userOptIn = userOptInDAO.get(new UserOptinId(optInId));
        if(userOptIn == null) {
            throw AppErrors.INSTANCE.invalidResourceRequest().exception();
        }
    }

    private void validateNecessaryFields(Long userId, UserOptin userOptIn) {
        checkUserValid(userId);
        checkUserOptInNotExists(userId, userOptIn);
    }

    private void validateUnnecessaryFields(UserOptin userOptIn) {
        if(userOptIn.getCreatedTime() != null) {
            throw AppErrors.INSTANCE.unnecessaryParameterField("userOptIn.createdTime").exception();
        }
        if(userOptIn.getUpdatedTime() != null) {
            throw AppErrors.INSTANCE.unnecessaryParameterField("userOptIn.updatedTime").exception();
        }
    }

    private void checkUserOptInNotExists(Long userId, UserOptin userOptIn) {

    }
}
