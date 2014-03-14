/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.service.validator.impl;

import com.junbo.identity.data.dao.UserTosAcceptanceDAO;
import com.junbo.identity.rest.service.validator.UserTosAcceptanceValidator;
import com.junbo.identity.spec.error.AppErrors;
import com.junbo.identity.spec.model.user.UserTosAcceptance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Created by liangfu on 2/28/14.
 */
@Component
public class UserTosAcceptanceValidatorImpl extends CommonValidator implements UserTosAcceptanceValidator {
    @Autowired
    private UserTosAcceptanceDAO userTosAcceptanceDAO;

    @Override
    public void validateCreate(Long userId, UserTosAcceptance userTosAcceptance) {
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
    public void validateUpdate(Long userId, Long userTosId, UserTosAcceptance userTosAcceptance) {
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

        UserTosAcceptance userTosAcceptance = userTosAcceptanceDAO.get(userTosId);
        if(userTosAcceptance == null) {
            throw AppErrors.INSTANCE.invalidResourceRequest().exception();
        }

        if(!userId.equals(userTosAcceptance.getUserId().getValue())) {
            throw AppErrors.INSTANCE.inputParametersMismatch("userId", "UserTosId").exception();
        }
    }

    private void validateNecessaryFields(Long userId, UserTosAcceptance userTosAcceptance) {
        checkUserValid(userId);
        if(userTosAcceptance.getUserId() == null) {
            throw AppErrors.INSTANCE.missingParameterField("userTosAcceptance.userId").exception();
        }
        if(StringUtils.isEmpty(userTosAcceptance.getTos())) {
            throw AppErrors.INSTANCE.missingParameterField("userTosAcceptance.tos").exception();
        }
        if(userTosAcceptance.getDateAccepted() == null) {
            throw AppErrors.INSTANCE.missingParameterField("userTosAcceptance.dateAccepted").exception();
        }
        if(!userId.equals(userTosAcceptance.getUserId().getValue())) {
            throw AppErrors.INSTANCE.unmatchedValue("userId", "userTosAcceptance.userId").exception();
        }
        checkTosAcceptanceNotExists(userId, userTosAcceptance);
    }

    private void validateUnnecessaryFields(UserTosAcceptance userTosAcceptance) {
        if(userTosAcceptance.getCreatedTime() != null) {
            throw AppErrors.INSTANCE.unnecessaryParameterField("userTosAcceptance.createdTime").exception();
        }
        if(userTosAcceptance.getUpdatedTime() != null) {
            throw AppErrors.INSTANCE.unnecessaryParameterField("userTosAcceptance.updatedTime").exception();
        }
    }

    private void checkTosAcceptanceNotExists(Long userId, UserTosAcceptance userTosAcceptance) {
        List<UserTosAcceptance> userTosAcceptanceList =
                userTosAcceptanceDAO.findByUserId(userId, userTosAcceptance.getTos());
        if(!CollectionUtils.isEmpty(userTosAcceptanceList)) {
            for(UserTosAcceptance tosAcceptance : userTosAcceptanceList) {
                if(userTosAcceptance.getId() == null ||
                  !tosAcceptance.getId().getValue().equals(userTosAcceptance.getId().getValue())) {
                    throw AppErrors.INSTANCE.userTosAlreadyAccepted(userTosAcceptance.getTos()).exception();
                }
            }
        }
    }
}
