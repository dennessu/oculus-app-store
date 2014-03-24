/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.core.service.validator.impl;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserOptinId;
import com.junbo.identity.data.repository.UserOptinRepository;
import com.junbo.identity.core.service.validator.UserOptinValidator;
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
    private UserOptinRepository userOptInRepository;

    @Override
    public void validateCreate(UserId userId, UserOptin userOptin) {
        if(userId == null || userOptin == null) {
            throw AppErrors.INSTANCE.invalidNullEmptyInputParam().exception();
        }
        validateNecessaryFields(userId, userOptin);
        validateUnnecessaryFields(userOptin);
        if(userOptin.getResourceAge() != null) {
            throw AppErrors.INSTANCE.unnecessaryParameterField("userOptin.resourceAge").exception();
        }
        if(userOptin.getId() != null) {
            throw AppErrors.INSTANCE.unnecessaryParameterField("userOptin.id").exception();
        }
    }

    @Override
    public void validateUpdate(UserId userId, UserOptinId userOptinId, UserOptin userOptin) {
        if(userId == null || userOptinId == null || userOptin == null) {
            throw AppErrors.INSTANCE.invalidNullEmptyInputParam().exception();
        }
        validateNecessaryFields(userId, userOptin);
        validateUnnecessaryFields(userOptin);
        if(userOptin.getResourceAge() == null) {
            throw AppErrors.INSTANCE.missingParameterField("userOptin.resourceAge").exception();
        }
        if(userOptin.getId() == null) {
            throw AppErrors.INSTANCE.missingParameterField("userOptin.id").exception();
        }
    }

    @Override
    public void validateDelete(UserId userId, UserOptinId userOptinId) {
        validateResourceAccessible(userId, userOptinId);
    }

    @Override
    public void validateResourceAccessible(UserId userId, UserOptinId userOptinId) {
        checkUserValid(userId);

        UserOptin userOptIn = userOptInRepository.get(userOptinId);
        if(userOptIn == null) {
            throw AppErrors.INSTANCE.invalidResourceRequest().exception();
        }
    }

    private void validateNecessaryFields(UserId userId, UserOptin userOptIn) {
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

    private void checkUserOptInNotExists(UserId userId, UserOptin userOptIn) {

    }
}
