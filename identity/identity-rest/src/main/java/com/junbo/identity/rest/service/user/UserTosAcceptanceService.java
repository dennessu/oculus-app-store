/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.service.user;

import com.junbo.identity.spec.model.user.UserTosAcceptance;

import java.util.List;

/**
 * Created by liangfu on 2/20/14.
 */
public interface UserTosAcceptanceService {
    UserTosAcceptance save(Long userId, UserTosAcceptance userTosAcceptance);
    UserTosAcceptance update(Long userId, Long userTosId, UserTosAcceptance userTosAcceptance);
    UserTosAcceptance get(Long userId, Long userTosId);
    List<UserTosAcceptance> getByUserId(Long userId, String tos);
    void delete(Long userId, Long userTosId);
}
