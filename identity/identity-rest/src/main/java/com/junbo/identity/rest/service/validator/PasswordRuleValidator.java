/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.service.validator;

import com.junbo.identity.spec.model.password.PasswordRule;

/**
 * Created by liangfu on 2/28/14.
 */
public interface PasswordRuleValidator {
    void validateCreate(PasswordRule passwordRule);
    void validateUpdate(Long id, PasswordRule passwordRule);
    void validateDelete(Long id);
}
