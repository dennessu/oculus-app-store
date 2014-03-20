/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.core.service.user;

import com.junbo.identity.spec.model.users.UserOptin;

import java.util.List;

/**
 * Created by liangfu on 2/20/14.
 */
public interface UserOptinService {
    UserOptin save(Long userId, UserOptin userOptIn);
    UserOptin update(Long userId, Long userOptInId, UserOptin userOptIn);
    UserOptin get(Long userId, Long userOptInId);
    List<UserOptin> getByUserId(Long userId, String type);
    void delete(Long userId, Long userOptInId);
}
