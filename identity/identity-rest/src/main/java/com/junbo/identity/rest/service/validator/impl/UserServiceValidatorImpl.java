/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.service.validator.impl;

import com.junbo.identity.data.entity.user.UserStatus;
import com.junbo.identity.rest.service.password.PasswordService;
import com.junbo.identity.rest.service.validator.UserServiceValidator;
import com.junbo.identity.spec.error.AppErrors;
import com.junbo.identity.spec.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * User service validator.
 */
@Component
class UserServiceValidatorImpl extends CommonValidator implements UserServiceValidator {
    @Autowired
    private PasswordService passwordService;

    @Override
    public void validateCreate(User user) {
        validateUserInfo(user);

        if(user.getId() != null) {
            throw AppErrors.INSTANCE.unnecessaryParameterField("id").exception();
        }
        if(user.getResourceAge() != null) {
            throw AppErrors.INSTANCE.unnecessaryParameterField("resourceAge").exception();
        }
        if(StringUtils.isEmpty(user.getPassword())) {
            throw AppErrors.INSTANCE.missingParameterField("password").exception();
        }

        if(user.getStatus().equals(UserStatus.BANNED.toString()) ||
           user.getStatus().equals(UserStatus.DELETED.toString())) {
            throw AppErrors.INSTANCE.invalidUserStatus("Create User can't accept " + user.getStatus() + " status.")
                    .exception();
        }

        // Check userName valid
        if(!CollectionUtils.isEmpty(userDAO.findByUserName(user.getUserName(), UserStatus.ACTIVE.toString()))) {
            throw AppErrors.INSTANCE.userAlreadyExists(user.getUserName()).exception();
        }
        if(!CollectionUtils.isEmpty(userDAO.findByUserName(user.getUserName(), UserStatus.BANNED.toString()))) {
            throw AppErrors.INSTANCE.userAlreadyBanned(user.getUserName()).exception();
        }
        if(!CollectionUtils.isEmpty(userDAO.findByUserName(user.getUserName(), UserStatus.SUSPEND.toString()))) {
            throw AppErrors.INSTANCE.userPendingForConfirmation(user.getUserName()).exception();
        }

        passwordService.validatePassword(user.getPassword());
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
        if(!StringUtils.isEmpty(user.getPassword())) {
            throw AppErrors.INSTANCE.unnecessaryParameterField("user.password").exception();
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
        User existingUser = userDAO.getUser(id);
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
        if(StringUtils.isEmpty(user.getUserName())) {
            throw AppErrors.INSTANCE.missingParameterField("userName").exception();
        }
        if(StringUtils.isEmpty(user.getStatus())) {
            throw AppErrors.INSTANCE.missingParameterField("status").exception();
        }
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
