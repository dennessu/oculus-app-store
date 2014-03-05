/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.service.validator;

import com.junbo.identity.spec.model.user.UserOptIn;

/**
 * Created by liangfu on 2/28/14.
 */
public interface UserOptInValidator {
    void validateCreate(Long userId, UserOptIn userOptIn);
    void validateUpdate(Long userId, Long optInId, UserOptIn userOptIn);
    void validateDelete(Long userId, Long optInId);
    void validateResourceAccessible(Long userId, Long optInId);
}
