/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.service.validator;

import com.junbo.identity.spec.model.user.UserTosAcceptance;

/**
 * Created by liangfu on 2/28/14.
 */
public interface UserTosAcceptanceValidator {
    void validateCreate(Long userId, UserTosAcceptance userTosAcceptance);
    void validateUpdate(Long userId, Long userTosId, UserTosAcceptance userTosAcceptance);
    void validateDelete(Long userId, Long userTosId);
    void validateResourceAccessible(Long userId, Long userTosId);
}
