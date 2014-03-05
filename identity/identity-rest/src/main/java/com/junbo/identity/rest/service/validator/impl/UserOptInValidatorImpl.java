/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.service.validator.impl;

import com.junbo.identity.data.dao.UserOptInDAO;
import com.junbo.identity.rest.service.validator.UserOptInValidator;
import com.junbo.identity.spec.error.AppErrors;
import com.junbo.identity.spec.model.user.UserOptIn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Created by liangfu on 2/28/14.
 */
@Component
public class UserOptInValidatorImpl extends CommonValidator implements UserOptInValidator {
    @Autowired
    private UserOptInDAO userOptInDAO;

    @Override
    public void validateCreate(Long userId, UserOptIn userOptIn) {
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
    public void validateUpdate(Long userId, Long optInId, UserOptIn userOptIn) {
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

        UserOptIn userOptIn = userOptInDAO.get(optInId);
        if(userOptIn == null) {
            throw AppErrors.INSTANCE.invalidResourceRequest().exception();
        }

        if(!userId.equals(userOptIn.getUserId().getValue())) {
            throw AppErrors.INSTANCE.inputParametersMismatch("userId", "userOptIn.userId").exception();
        }
    }

    private void validateNecessaryFields(Long userId, UserOptIn userOptIn) {
        checkUserValid(userId);
        if(userOptIn.getUserId() == null) {
            throw AppErrors.INSTANCE.missingParameterField("userOptIn.userId").exception();
        }
        if(StringUtils.isEmpty(userOptIn.getType())) {
            throw AppErrors.INSTANCE.missingParameterField("userOptIn.type").exception();
        }
        checkUserOptInNotExists(userId, userOptIn);
    }

    private void validateUnnecessaryFields(UserOptIn userOptIn) {
        if(userOptIn.getCreatedTime() != null) {
            throw AppErrors.INSTANCE.unnecessaryParameterField("userOptIn.createdTime").exception();
        }
        if(userOptIn.getUpdatedTime() != null) {
            throw AppErrors.INSTANCE.unnecessaryParameterField("userOptIn.updatedTime").exception();
        }
    }

    private void checkUserOptInNotExists(Long userId, UserOptIn userOptIn) {
        List<UserOptIn> userOptIns = userOptInDAO.findByUser(userId, userOptIn.getType());

        if(!CollectionUtils.isEmpty(userOptIns)) {
            for(UserOptIn temp : userOptIns) {
                if(userOptIn.getId() == null || !temp.getId().getValue().equals(userOptIn.getId().getValue())) {
                    throw AppErrors.INSTANCE.userOptInAlreadyExists(temp.getType()).exception();
                }
            }
        }
    }
}
