/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.core.service.validator.impl;

import com.junbo.common.id.UserAuthenticatorId;
import com.junbo.common.id.UserId;
import com.junbo.identity.data.repository.UserAuthenticatorRepository;
import com.junbo.identity.core.service.validator.UserAuthenticatorValidator;
import com.junbo.identity.spec.error.AppErrors;
import com.junbo.identity.spec.options.list.UserAuthenticatorListOption;
import com.junbo.identity.spec.model.users.UserAuthenticator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Created by liangfu on 3/3/14.
 */
@Component
public class UserAuthenticatorValidatorImpl extends CommonValidator implements UserAuthenticatorValidator {
    @Autowired
    private UserAuthenticatorRepository userAuthenticatorRepository;

    @Override
    public void validateCreate(UserId userId, UserAuthenticator userFederation) {
        if(userId == null || userFederation == null) {
            throw AppErrors.INSTANCE.invalidNullEmptyInputParam().exception();
        }
        validateNecessaryFields(userId, userFederation);
        validateUnnecessaryFields(userFederation);
        if(userFederation.getResourceAge() != null) {
            throw AppErrors.INSTANCE.unnecessaryParameterField("userFederation.resourceAge").exception();
        }
        if(userFederation.getId() != null) {
            throw AppErrors.INSTANCE.unnecessaryParameterField("userFederation.id").exception();
        }
    }

    @Override
    public void validateUpdate(UserId userId, UserAuthenticatorId userAuthenticatorId,
                               UserAuthenticator userAuthenticator) {
        if(userId == null || userAuthenticatorId == null || userAuthenticator == null) {
            throw AppErrors.INSTANCE.invalidNullEmptyInputParam().exception();
        }
        validateNecessaryFields(userId, userAuthenticator);
        validateUnnecessaryFields(userAuthenticator);
        if(userAuthenticator.getResourceAge() == null) {
            throw AppErrors.INSTANCE.missingParameterField("userFederation.resourceAge").exception();
        }
        if(userAuthenticator.getId() == null) {
            throw AppErrors.INSTANCE.missingParameterField("userFederation.id").exception();
        }
    }

    @Override
    public void validateDelete(UserId userId, UserAuthenticatorId userAuthenticatorId) {
        validateResourceAccessible(userId, userAuthenticatorId);
    }

    @Override
    public void validateResourceAccessible(UserId userId, UserAuthenticatorId userAuthenticatorId) {
        checkUserValid(userId);

        UserAuthenticator userFederation = userAuthenticatorRepository.get(userAuthenticatorId);
        if(userFederation == null) {
            throw AppErrors.INSTANCE.invalidResourceRequest().exception();
        }

        if(!userId.equals(userFederation.getUserId().getValue())) {
            throw AppErrors.INSTANCE.inputParametersMismatch("userId", "userFederation.userId").exception();
        }
    }

    private void validateNecessaryFields(UserId userId, UserAuthenticator userAuthenticator) {
        checkUserValid(userId);
        if(userAuthenticator.getUserId() == null) {
            throw AppErrors.INSTANCE.missingParameterField("userFederation.userId").exception();
        }
        if(StringUtils.isEmpty(userAuthenticator.getType())) {
            throw AppErrors.INSTANCE.missingParameterField("userFederation.type").exception();
        }
        if(StringUtils.isEmpty(userAuthenticator.getValue())) {
            throw AppErrors.INSTANCE.missingParameterField("userFederation.value").exception();
        }
        checkUserAuthenticatorNotExists(userId, userAuthenticator);
    }

    private void validateUnnecessaryFields(UserAuthenticator userFederation) {
        if(userFederation.getCreatedTime() != null) {
            throw AppErrors.INSTANCE.unnecessaryParameterField("userFederation.createdTime").exception();
        }
        if(userFederation.getUpdatedTime() != null) {
            throw AppErrors.INSTANCE.unnecessaryParameterField("userFederation.updatedTime").exception();
        }
    }

    private void checkUserAuthenticatorNotExists(UserId userId, UserAuthenticator userFederation) {
        UserAuthenticatorListOption getOption = new UserAuthenticatorListOption();
        getOption.setUserId(userId);
        List<UserAuthenticator> userFederations = userAuthenticatorRepository.search(getOption);

        if(!CollectionUtils.isEmpty(userFederations)) {
            for(UserAuthenticator temp : userFederations) {
                if(userFederation.getId() == null
                || !temp.getId().getValue().equals(userFederation.getId().getValue())) {
                    throw AppErrors.INSTANCE.userFederationAlreadyExists(temp.getType()).exception();
                }
            }
        }
    }
}
