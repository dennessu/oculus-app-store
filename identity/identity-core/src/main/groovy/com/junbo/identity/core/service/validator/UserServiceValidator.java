/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.core.service.validator;

import com.junbo.common.id.UserId;
import com.junbo.identity.spec.model.users.User;

/**
 * User service validator.
 */
public interface UserServiceValidator {
    void validateCreate(User user);
    void validateUpdate(UserId id, User user);
    void validateDelete(UserId id);
}
