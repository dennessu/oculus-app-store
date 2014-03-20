/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.core.service.validator.impl;

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
    private UserTosRepository userTosAcceptanceRepository;

    @Override
    public void validateCreate(Long userId, UserTos userTosAcceptance) {
        if(userId == null || userTosAcceptance == null) {
            throw AppErrors.INSTANCE.invalidNullEmptyInputParam().exception();
        }
        validateNecessaryFields(userId, userTosAcceptance);
        validateUnnecessaryFields(userTosAcceptance);
        if(userTosAcceptance.getResourceAge() != null) {
            throw AppErrors.INSTANCE.unnecessaryParameterField("userTosAcceptance.resourceAge").exception();
        }
        if(userTosAcceptance.getId() != null) {
            throw AppErrors.INSTANCE.unnecessaryParameterField("userTosAcceptance.id").exception();
        }
    }

    @Override
    public void validateUpdate(Long userId, Long userTosId, UserTos userTosAcceptance) {
        if(userId == null || userTosId == null || userTosAcceptance == null) {
            throw AppErrors.INSTANCE.invalidNullEmptyInputParam().exception();
        }
        validateNecessaryFields(userId, userTosAcceptance);
        validateUnnecessaryFields(userTosAcceptance);
        validateResourceAccessible(userId, userTosId);
        if(userTosAcceptance.getResourceAge() == null) {
            throw AppErrors.INSTANCE.missingParameterField("userTosAcceptance.resourceAge").exception();
        }
        if(userTosAcceptance.getId() == null) {
            throw AppErrors.INSTANCE.missingParameterField("userTosAcceptance.id").exception();
        }
    }

    @Override
    public void validateDelete(Long userId, Long userTosId) {
        validateResourceAccessible(userId, userTosId);
    }

    @Override
    public void validateResourceAccessible(Long userId, Long userTosId) {
        checkUserValid(userId);

        UserTos userTosAcceptance = userTosAcceptanceRepository.get(new UserTosId(userTosId));
        if(userTosAcceptance == null) {
            throw AppErrors.INSTANCE.invalidResourceRequest().exception();
        }
    }

    private void validateNecessaryFields(Long userId, UserTos userTosAcceptance) {
        checkUserValid(userId);

        checkTosAcceptanceNotExists(userId, userTosAcceptance);
    }

    private void validateUnnecessaryFields(UserTos userTosAcceptance) {
        if(userTosAcceptance.getCreatedTime() != null) {
            throw AppErrors.INSTANCE.unnecessaryParameterField("userTosAcceptance.createdTime").exception();
        }
        if(userTosAcceptance.getUpdatedTime() != null) {
            throw AppErrors.INSTANCE.unnecessaryParameterField("userTosAcceptance.updatedTime").exception();
        }
    }

    private void checkTosAcceptanceNotExists(Long userId, UserTos userTosAcceptance) {

    }
}
