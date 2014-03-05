/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.service.user;

import com.junbo.identity.spec.model.user.UserOptIn;

import java.util.List;

/**
 * Created by liangfu on 2/20/14.
 */
public interface UserOptInService {
    UserOptIn save(Long userId, UserOptIn userOptIn);
    UserOptIn update(Long userId, Long userOptInId, UserOptIn userOptIn);
    UserOptIn get(Long userId, Long userOptInId);
    List<UserOptIn> getByUserId(Long userId, String type);
    void delete(Long userId, Long userOptInId);
}
