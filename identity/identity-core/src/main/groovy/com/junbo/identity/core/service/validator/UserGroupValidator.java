/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.core.service.validator;

import com.junbo.common.id.UserGroupId;
import com.junbo.common.id.UserId;
import com.junbo.identity.spec.model.users.UserGroup;

/**
 * Created by liangfu on 3/25/14.
 */
public interface UserGroupValidator {
    void validateCreate(UserId userId, UserGroup userGroup);
    void validateUpdate(UserId userId, UserGroupId userGroupId, UserGroup userGroup);
    void validateDelete(UserId userId, UserGroupId userGroupId);
    void validateResourceAccessible(UserId userId, UserGroupId userGroupId);
}
