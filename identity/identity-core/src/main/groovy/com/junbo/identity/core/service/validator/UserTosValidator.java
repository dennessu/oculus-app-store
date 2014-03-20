/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.core.service.validator;

import com.junbo.identity.spec.model.users.UserTos;

/**
 * Created by liangfu on 2/28/14.
 */
public interface UserTosValidator {
    void validateCreate(Long userId, UserTos userTosAcceptance);
    void validateUpdate(Long userId, Long userTosId, UserTos userTosAcceptance);
    void validateDelete(Long userId, Long userTosId);
    void validateResourceAccessible(Long userId, Long userTosId);
}
