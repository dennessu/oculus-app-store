/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.core.service.validator.impl;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserPinId;
import com.junbo.identity.core.service.validator.UserPinValidator;
import com.junbo.identity.spec.model.users.UserPin;
import com.junbo.identity.spec.options.list.UserPinListOptions;
import org.springframework.stereotype.Component;

/**
 * Created by liangfu on 3/24/14.
 */
@Component
public class UserPinValidatorImpl extends CommonValidator implements UserPinValidator {
    @Override
    public void validateGet(UserId userId, UserPinId userPinId) {

    }

    @Override
    public void validateCreate(UserId userId, UserPin userPin) {

    }

    @Override
    public void validateSearch(UserId userId, UserPinListOptions getOption) {

    }
}
