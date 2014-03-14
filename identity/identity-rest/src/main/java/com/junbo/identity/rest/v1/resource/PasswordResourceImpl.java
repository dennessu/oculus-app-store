/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.v1.resource;

import com.junbo.common.id.PasswordRuleId;
import com.junbo.identity.spec.model.password.PasswordRule;
import com.junbo.identity.spec.resource.PasswordResource;
import com.junbo.langur.core.promise.Promise;

/**
 * Created by liangfu on 3/14/14.
 */
public class PasswordResourceImpl implements PasswordResource {
    @Override
    public Promise<PasswordRule> getPasswordRule(PasswordRuleId passwordRuleId) {
        return null;
    }

    @Override
    public Promise<PasswordRule> postPasswordRule(PasswordRule passwordRule) {
        return null;
    }

    @Override
    public Promise<PasswordRule> updatePasswordRule(PasswordRuleId passwordRuleId, PasswordRule passwordRule) {
        return null;
    }
}
