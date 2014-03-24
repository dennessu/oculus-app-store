/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.core.service.validator;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserTosId;
import com.junbo.identity.spec.model.users.UserTos;

/**
 * Created by liangfu on 2/28/14.
 */
public interface UserTosValidator {
    void validateCreate(UserId userId, UserTos userTos);
    void validateUpdate(UserId userId, UserTosId userTosId, UserTos userTos);
    void validateDelete(UserId userId, UserTosId userTosId);
    void validateResourceAccessible(UserId userId, UserTosId userTosId);
}
