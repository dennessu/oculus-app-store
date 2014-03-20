/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.service.validator.impl;

import com.junbo.common.id.SecurityQuestionId;
import com.junbo.identity.rest.service.validator.SecurityQuestionValidator;
import com.junbo.identity.spec.error.AppErrors;
import com.junbo.identity.spec.model.domaindata.SecurityQuestion;
import com.junbo.identity.spec.options.SecurityQuestionListOptions;
import org.springframework.util.StringUtils;

/**
 * Created by haomin on 14-3-19.
 */
public class SecurityQuestionValidatorImpl implements SecurityQuestionValidator {
    @Override
    public void validateCreate(SecurityQuestion securityQuestion) {
        this.validateSecurityQuestion(securityQuestion);
        if (securityQuestion.getId() != null) {
            throw AppErrors.INSTANCE.invalidParameter("SecurityQuestion.id").exception();
        }
    }

    @Override
    public void validateUpdate(SecurityQuestionId id, SecurityQuestion securityQuestion) {
        this.validateSecurityQuestion(securityQuestion);
        this.validataPatch(id, securityQuestion);
    }

    @Override
    public void validateGet(SecurityQuestionId id) {
        if(id == null || id.getValue() == null) {
            throw AppErrors.INSTANCE.invalidRequest().exception();
        }
    }

    @Override
    public void validateSearch(SecurityQuestionListOptions options) {

    }

    @Override
    public void validataPatch(SecurityQuestionId id, SecurityQuestion securityQuestion) {
        if(id == null || id.getValue() == null) {
            throw AppErrors.INSTANCE.invalidRequest().exception();
        }
        if(securityQuestion.getId() == null || securityQuestion.getId().getValue() == null) {
            throw AppErrors.INSTANCE.missingParameterField("id").exception();
        }
        if(!id.getValue().equals(securityQuestion.getId().getValue())) {
            throw AppErrors.INSTANCE.inputParametersMismatch("SecurityQuestionId", "SecurityQuestion.id").exception();
        }
    }

    private void validateSecurityQuestion(SecurityQuestion securityQuestion) {
        if (securityQuestion == null) {
            throw AppErrors.INSTANCE.invalidRequest().exception();
        }

        if (StringUtils.isEmpty(securityQuestion.getValue())) {
            throw AppErrors.INSTANCE.missingParameterField("SecurityQuestion.value").exception();
        }
        if (securityQuestion.getActive() == null) {
            throw AppErrors.INSTANCE.missingParameterField("SecurityQuestion.active").exception();
        }
    }
}
