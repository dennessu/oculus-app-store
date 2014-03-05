/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.service.user;


import com.junbo.identity.spec.model.user.UserProfile;

import java.util.List;

/**
 * interface for UserProfileService.
 */
public interface UserProfileService {
    UserProfile save(Long userId, UserProfile userProfile);
    UserProfile update(Long userId, Long profileId, UserProfile userProfile);
    UserProfile get(Long userId, Long profileId);
    List<UserProfile> getByUserId(Long userId, String type);
    void deleteProfile(Long userId, Long profileId);
}
