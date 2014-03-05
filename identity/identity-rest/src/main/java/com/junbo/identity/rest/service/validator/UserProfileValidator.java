/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.service.validator;

import com.junbo.identity.spec.model.user.UserProfile;

/**
 * Created by liangfu on 2/28/14.
 */
public interface UserProfileValidator {
    void validateCreate(Long userId, UserProfile userProfile);
    void validateUpdate(Long userId, Long profileId, UserProfile userProfile);
    void validateDelete(Long userId, Long profileId);
    void validateResourceAccessible(Long userId, Long profileId);
}
