/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.core.service.user;

import com.junbo.identity.spec.model.users.UserTos;

import java.util.List;

/**
 * Created by liangfu on 2/20/14.
 */
public interface UserTosService {
    UserTos save(Long userId, UserTos userTosAcceptance);
    UserTos update(Long userId, Long userTosId, UserTos userTosAcceptance);
    UserTos get(Long userId, Long userTosId);
    List<UserTos> getByUserId(Long userId, String tos);
    void delete(Long userId, Long userTosId);
}
