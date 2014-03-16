/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.service.validator.impl;

import com.junbo.common.id.UserAuthenticatorId;
import com.junbo.common.id.UserId;
import com.junbo.identity.data.dao.UserAuthenticatorDAO;
import com.junbo.identity.rest.service.validator.UserAuthenticatorValidator;
import com.junbo.identity.spec.error.AppErrors;
import com.junbo.identity.spec.model.options.UserAuthenticatorGetOption;
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
    private UserAuthenticatorDAO userFederationDAO;

    @Override
    public void validateCreate(Long userId, UserAuthenticator userFederation) {
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
    public void validateUpdate(Long userId, Long federationId, UserAuthenticator userFederation) {
        if(userId == null || federationId == null || userFederation == null) {
            throw AppErrors.INSTANCE.invalidNullEmptyInputParam().exception();
        }
        validateNecessaryFields(userId, userFederation);
        validateUnnecessaryFields(userFederation);
        if(userFederation.getResourceAge() == null) {
            throw AppErrors.INSTANCE.missingParameterField("userFederation.resourceAge").exception();
        }
        if(userFederation.getId() == null) {
            throw AppErrors.INSTANCE.missingParameterField("userFederation.id").exception();
        }
    }

    @Override
    public void validateDelete(Long userId, Long federationId) {
        validateResourceAccessible(userId, federationId);
    }

    @Override
    public void validateResourceAccessible(Long userId, Long federationId) {
        checkUserValid(userId);

        UserAuthenticator userFederation = userFederationDAO.get(new UserAuthenticatorId(federationId));
        if(userFederation == null) {
            throw AppErrors.INSTANCE.invalidResourceRequest().exception();
        }

        if(!userId.equals(userFederation.getUserId().getValue())) {
            throw AppErrors.INSTANCE.inputParametersMismatch("userId", "userFederation.userId").exception();
        }
    }

    private void validateNecessaryFields(Long userId, UserAuthenticator userFederation) {
        checkUserValid(userId);
        if(userFederation.getUserId() == null) {
            throw AppErrors.INSTANCE.missingParameterField("userFederation.userId").exception();
        }
        if(StringUtils.isEmpty(userFederation.getType())) {
            throw AppErrors.INSTANCE.missingParameterField("userFederation.type").exception();
        }
        if(StringUtils.isEmpty(userFederation.getValue())) {
            throw AppErrors.INSTANCE.missingParameterField("userFederation.value").exception();
        }
        checkUserFederationNotExists(userId, userFederation);
    }

    private void validateUnnecessaryFields(UserAuthenticator userFederation) {
        if(userFederation.getCreatedTime() != null) {
            throw AppErrors.INSTANCE.unnecessaryParameterField("userFederation.createdTime").exception();
        }
        if(userFederation.getUpdatedTime() != null) {
            throw AppErrors.INSTANCE.unnecessaryParameterField("userFederation.updatedTime").exception();
        }
    }

    private void checkUserFederationNotExists(Long userId, UserAuthenticator userFederation) {
        UserAuthenticatorGetOption getOption = new UserAuthenticatorGetOption();
        getOption.setUserId(new UserId(userId));
        List<UserAuthenticator> userFederations = userFederationDAO.search(getOption);

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
