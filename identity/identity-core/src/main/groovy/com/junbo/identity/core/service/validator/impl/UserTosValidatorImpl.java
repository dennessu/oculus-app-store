/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.core.service.validator.impl;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserTosId;
import com.junbo.identity.data.repository.UserTosRepository;
import com.junbo.identity.core.service.validator.UserTosValidator;
import com.junbo.identity.spec.error.AppErrors;
import com.junbo.identity.spec.model.users.UserTos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by liangfu on 2/28/14.
 */
@Component
public class UserTosValidatorImpl extends CommonValidator implements UserTosValidator {
    @Autowired
    private UserTosRepository userTosRepository;

    @Override
    public void validateCreate(UserId userId, UserTos userTos) {
        if(userId == null || userTos == null) {
            throw AppErrors.INSTANCE.invalidNullEmptyInputParam().exception();
        }
        validateNecessaryFields(userId, userTos);
        validateUnnecessaryFields(userTos);
        if(userTos.getResourceAge() != null) {
            throw AppErrors.INSTANCE.unnecessaryParameterField("userTos.resourceAge").exception();
        }
        if(userTos.getId() != null) {
            throw AppErrors.INSTANCE.unnecessaryParameterField("userTos.id").exception();
        }
    }

    @Override
    public void validateUpdate(UserId userId, UserTosId userTosId, UserTos userTos) {
        if(userId == null || userTosId == null || userTos == null) {
            throw AppErrors.INSTANCE.invalidNullEmptyInputParam().exception();
        }
        validateNecessaryFields(userId, userTos);
        validateUnnecessaryFields(userTos);
        validateResourceAccessible(userId, userTosId);
        if(userTos.getResourceAge() == null) {
            throw AppErrors.INSTANCE.missingParameterField("userTos.resourceAge").exception();
        }
        if(userTos.getId() == null) {
            throw AppErrors.INSTANCE.missingParameterField("userTos.id").exception();
        }
    }

    @Override
    public void validateDelete(UserId userId, UserTosId userTosId) {
        validateResourceAccessible(userId, userTosId);
    }

    @Override
    public void validateResourceAccessible(UserId userId, UserTosId userTosId) {
        checkUserValid(userId);

        UserTos userTosAcceptance = userTosRepository.get(userTosId);
        if(userTosAcceptance == null) {
            throw AppErrors.INSTANCE.invalidResourceRequest().exception();
        }
    }

    private void validateNecessaryFields(UserId userId, UserTos userTos) {
        checkUserValid(userId);

        checkTosAcceptanceNotExists(userId, userTos);
    }

    private void validateUnnecessaryFields(UserTos userTosAcceptance) {
        if(userTosAcceptance.getCreatedTime() != null) {
            throw AppErrors.INSTANCE.unnecessaryParameterField("userTosAcceptance.createdTime").exception();
        }
        if(userTosAcceptance.getUpdatedTime() != null) {
            throw AppErrors.INSTANCE.unnecessaryParameterField("userTosAcceptance.updatedTime").exception();
        }
    }

    private void checkTosAcceptanceNotExists(UserId userId, UserTos userTos) {

    }
}
