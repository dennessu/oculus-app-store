/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.core.service.validator;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserPinId;
import com.junbo.identity.spec.model.users.UserPin;
import com.junbo.identity.spec.options.list.UserPinListOption;

/**
 * Created by liangfu on 3/24/14.
 */
public interface UserPinValidator {
    void validateGet(UserId userId, UserPinId userPinId);
    void validateCreate(UserId userId, UserPin userPin);
    void validateSearch(UserId userId, UserPinListOption getOption);
}
