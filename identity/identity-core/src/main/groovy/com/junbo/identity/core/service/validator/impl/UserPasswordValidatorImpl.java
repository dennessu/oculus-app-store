/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.core.service.validator.impl;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserPasswordId;
import com.junbo.identity.core.service.validator.UserPasswordValidator;
import com.junbo.identity.spec.model.users.UserPassword;
import com.junbo.identity.spec.options.list.UserPasswordListOption;
import org.springframework.stereotype.Component;

/**
 * Created by liangfu on 3/24/14.
 */
@Component
public class UserPasswordValidatorImpl extends CommonValidator implements UserPasswordValidator {

    @Override
    public void validateGet(UserId userId, UserPasswordId userPasswordId) {
        return ;
    }

    @Override
    public void validateCreate(UserId userId, UserPassword userPassword) {
        return ;
    }

    @Override
    public void validateSearch(UserId userId, UserPasswordListOption getOption) {
        return ;
    }
}
