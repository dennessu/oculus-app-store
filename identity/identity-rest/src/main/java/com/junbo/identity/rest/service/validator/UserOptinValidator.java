/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.service.validator;

import com.junbo.identity.spec.model.users.UserOptin;

/**
 * Created by liangfu on 2/28/14.
 */
public interface UserOptinValidator {
    void validateCreate(Long userId, UserOptin userOptIn);
    void validateUpdate(Long userId, Long optInId, UserOptin userOptIn);
    void validateDelete(Long userId, Long optInId);
    void validateResourceAccessible(Long userId, Long optInId);
}
