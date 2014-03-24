/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.core.service.validator;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserPasswordId;
import com.junbo.identity.spec.model.users.UserPassword;
import com.junbo.identity.spec.options.list.UserPasswordListOption;

/**
 * Created by liangfu on 3/24/14.
 */
public interface UserPasswordValidator {
    void validateGet(UserId userId, UserPasswordId userPasswordId);
    void validateCreate(UserId userId, UserPassword userPassword);
    void validateSearch(UserId userId, UserPasswordListOption getOption);
}
