/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.service.validator;

import com.junbo.common.id.SecurityQuestionId;
import com.junbo.identity.spec.model.domaindata.SecurityQuestion;
import com.junbo.identity.spec.options.SecurityQuestionListOptions;

/**
 * Created by haomin on 14-3-19.
 */
public interface SecurityQuestionValidator {
    void validateCreate(SecurityQuestion securityQuestion);
    void validateUpdate(SecurityQuestionId id, SecurityQuestion securityQuestion);
    void validataPatch(SecurityQuestionId id, SecurityQuestion securityQuestion);
    void validateGet(SecurityQuestionId id);
    void validateSearch(SecurityQuestionListOptions options);
}
