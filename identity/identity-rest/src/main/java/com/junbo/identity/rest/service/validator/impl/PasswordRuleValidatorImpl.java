/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.service.validator.impl;

import com.junbo.identity.rest.service.validator.PasswordRuleValidator;
import com.junbo.identity.spec.error.AppErrors;
import com.junbo.identity.spec.model.password.PasswordRule;
import com.junbo.identity.spec.model.password.PasswordRuleDetail;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;

/**
 * Created by liangfu on 2/24/14.
 */
@Component
public class PasswordRuleValidatorImpl implements PasswordRuleValidator {
    public void validateCreate(PasswordRule passwordRule) {
        if(StringUtils.isEmpty(passwordRule.getPasswordStrength())) {
            throw AppErrors.INSTANCE.missingParameterField("passwordStrength").exception();
        }

        if(CollectionUtils.isEmpty(passwordRule.getAllowedCharacterSet())) {
            throw AppErrors.INSTANCE.missingParameterField("allowedCharacterSet").exception();
        }

        if(CollectionUtils.isEmpty(passwordRule.getNotAllowedCharacterSet())) {
            throw AppErrors.INSTANCE.missingParameterField("notAllowedCharacterSet").exception();
        }

        if(passwordRule.getId() != null) {
            throw AppErrors.INSTANCE.unnecessaryParameterField("self").exception();
        }

        validatePasswordRuleDetail(passwordRule.getPasswordRuleDetails());
    }

    public void validateUpdate(Long id, PasswordRule passwordRule) {
    }

    public void validateDelete(Long id) {
    }

    private void validatePasswordRuleDetail(List<PasswordRuleDetail> passwordRuleDetails) {
        if(CollectionUtils.isEmpty(passwordRuleDetails)) {
            throw AppErrors.INSTANCE.missingParameterField("details").exception();
        }

        Collections.sort(passwordRuleDetails, new PasswordRuleDetailComparator());

        for(int index = 1; index < passwordRuleDetails.size(); index ++) {
            if(passwordRuleDetails.get(index -1).equals(passwordRuleDetails.get(index))) {
                throw AppErrors.INSTANCE.duplicatePasswordRuleDetails().exception();
            }
        }
    }
}
