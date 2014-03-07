/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.resource;

import com.junbo.common.id.PasswordRuleId;
import com.junbo.identity.rest.service.password.PasswordService;
import com.junbo.identity.spec.error.AppErrors;
import com.junbo.identity.spec.model.password.PasswordRule;
import com.junbo.identity.spec.resource.PasswordResource;
import com.junbo.langur.core.promise.Promise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.ext.Provider;

/**
 * Created by liangfu on 2/19/14.
 */
@Provider
@Component
@org.springframework.context.annotation.Scope("prototype")
public class PasswordResourceImpl implements PasswordResource {
    @Autowired
    private PasswordService passwordService;

    @Override
    public Promise<PasswordRule> getPasswordRule(PasswordRuleId passwordRuleId) {
        return Promise.pure(passwordService.get(passwordRuleId.getValue()));
    }

    @Override
    public Promise<PasswordRule> postPasswordRule(PasswordRule passwordRule) {
        return Promise.pure(passwordService.save(passwordRule));
    }

    @Override
    public Promise<PasswordRule> updatePasswordRule(PasswordRuleId passwordRuleId, PasswordRule passwordRule) {
        if(passwordRule.getId() == null) {
            throw AppErrors.INSTANCE.missingParameterField("passwordRule.id").exception();
        }

        if(passwordRule.getId().getValue().equals(passwordRuleId.getValue())) {
            throw AppErrors.INSTANCE.unmatchedValue("passwordRuleId", "passwordRule.ID").exception();
        }
        return Promise.pure(passwordService.update(passwordRule));
    }
}
