/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.core.service.validator.impl;

import com.junbo.common.id.UserGroupId;
import com.junbo.common.id.UserId;
import com.junbo.identity.core.service.validator.UserGroupValidator;
import com.junbo.identity.spec.model.users.UserGroup;
import org.springframework.stereotype.Component;

/**
 * Created by liangfu on 3/25/14.
 */
@Component
public class UserGroupValidatorImpl extends CommonValidator implements UserGroupValidator {

    @Override
    public void validateCreate(UserId userId, UserGroup userGroup) {

    }

    @Override
    public void validateUpdate(UserId userId, UserGroupId userGroupId, UserGroup userGroup) {

    }

    @Override
    public void validateDelete(UserId userId, UserGroupId userGroupId) {

    }

    @Override
    public void validateResourceAccessible(UserId userId, UserGroupId userGroupId) {

    }
}
