/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.core.service.validator;

import com.junbo.common.id.UserAuthenticatorId;
import com.junbo.common.id.UserId;
import com.junbo.identity.spec.model.users.UserAuthenticator;

/**
 * Created by liangfu on 3/3/14.
 */
public interface UserAuthenticatorValidator {
    void validateCreate(UserId userId, UserAuthenticator userFederation);
    void validateUpdate(UserId userId, UserAuthenticatorId userAuthenticatorId, UserAuthenticator userAuthenticator);
    void validateDelete(UserId userId, UserAuthenticatorId userAuthenticatorId);
    void validateResourceAccessible(UserId userId, UserAuthenticatorId userAuthenticatorId);
}
