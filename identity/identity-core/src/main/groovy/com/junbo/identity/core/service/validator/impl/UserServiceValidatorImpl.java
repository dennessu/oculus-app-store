/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.core.service.validator.impl;

import com.junbo.common.id.UserId;
import com.junbo.identity.core.service.validator.UserServiceValidator;
import com.junbo.identity.spec.error.AppErrors;
import com.junbo.identity.spec.model.users.User;
import org.springframework.stereotype.Component;

/**
 * User service validator.
 */
@Component
class UserServiceValidatorImpl extends CommonValidator implements UserServiceValidator {

    @Override
    public void validateCreate(User user) {
        validateUserInfo(user);

        if(user.getId() != null) {
            throw AppErrors.INSTANCE.unnecessaryParameterField("id").exception();
        }
        if(user.getResourceAge() != null) {
            throw AppErrors.INSTANCE.unnecessaryParameterField("resourceAge").exception();
        }
    }

    @Override
    public void validateUpdate(Long id, User user) {
        validateUserExist(id);
        validateUserInfo(user);

        if(user.getId() == null) {
            throw AppErrors.INSTANCE.missingParameterField("user.id").exception();
        }
        if(!id.equals(user.getId().getValue())) {
            throw AppErrors.INSTANCE.inputParametersMismatch("id", "user.id").exception();
        }
        if(user.getResourceAge() == null) {
            throw AppErrors.INSTANCE.missingParameterField("user.resourceAge").exception();
        }
    }

    @Override
    public void validateDelete(Long id) {
        validateUserExist(id);
    }

    private void validateUserExist(Long id) {
        if(id == null) {
            throw AppErrors.INSTANCE.invalidNullEmptyInputParam().exception();
        }
        User existingUser = userRepository.get(new UserId(id));
        if(existingUser == null) {
            throw AppErrors.INSTANCE.notExistingUser("userId = " + id.toString()).exception();
        }
    }

    private void validateUserInfo(User user) {
        if(user == null) {
            throw AppErrors.INSTANCE.invalidNullEmptyInputParam().exception();
        }

        validateNecessaryFields(user);
        validateUnnecessaryFields(user);
    }

    private void validateNecessaryFields(User user) {

    }

    private void validateUnnecessaryFields(User user) {
        if(user.getUpdatedTime() != null) {
            throw AppErrors.INSTANCE.unnecessaryParameterField("updatedTime").exception();
        }
        if(user.getCreatedTime() != null) {
            throw AppErrors.INSTANCE.unnecessaryParameterField("createdTime").exception();
        }
    }
}
