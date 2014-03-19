/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.service.validator.impl;

import com.junbo.identity.rest.service.validator.SecurityQuestionValidator;
import com.junbo.identity.spec.model.domaindata.SecurityQuestion;
import com.junbo.identity.spec.options.SecurityQuestionListOptions;

/**
 * Created by haomin on 14-3-19.
 */
public class SecurityQuestionValidatorImpl implements SecurityQuestionValidator {
    @Override
    public void validateCreate(SecurityQuestion securityQuestion) {
    }

    @Override
    public void validateUpdate(Long id, SecurityQuestion securityQuestion) {
    }

    @Override
    public void validataGet(Long id) {
    }

    @Override
    public void validateSearch(SecurityQuestionListOptions options) {

    }
}
