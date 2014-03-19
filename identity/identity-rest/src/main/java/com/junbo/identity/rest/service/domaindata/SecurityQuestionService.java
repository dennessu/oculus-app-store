/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.service.domaindata;

import com.junbo.common.id.SecurityQuestionId;
import com.junbo.identity.spec.model.domaindata.SecurityQuestion;
import com.junbo.identity.spec.options.SecurityQuestionListOptions;

import java.util.List;

/**
 * Created by haomin on 14-3-19.
 */
public interface SecurityQuestionService {
    SecurityQuestion get(SecurityQuestionId securityQuestionId);
    SecurityQuestion create(SecurityQuestion securityQuestion);
    SecurityQuestion update(SecurityQuestionId securityQuestionId, SecurityQuestion securityQuestion);
    SecurityQuestion patch(SecurityQuestionId securityQuestionId, SecurityQuestion securityQuestion);
    List<SecurityQuestion> search(SecurityQuestionListOptions listOption);
}
