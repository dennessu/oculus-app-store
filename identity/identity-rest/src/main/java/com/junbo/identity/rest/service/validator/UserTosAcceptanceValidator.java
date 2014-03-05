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
    void validateUpdate(Long userId, Long userTosAcceptanceId, UserTosAcceptance userTosAcceptance);
    void validateDelete(Long userId, Long userTosAcceptanceId);
    void validateResourceAccessible(Long userId, Long userTosAcceptanceId);
}
