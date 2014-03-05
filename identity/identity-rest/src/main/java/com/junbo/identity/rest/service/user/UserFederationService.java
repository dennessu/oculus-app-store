/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.service.user;

import com.junbo.identity.spec.model.user.UserFederation;

import java.util.List;

/**
 * Created by liangfu on 2/20/14.
 */
public interface UserFederationService {
    UserFederation save(Long userId, UserFederation userFederation);
    UserFederation update(Long userId, Long federationId, UserFederation userFederation);
    UserFederation get(Long userId, Long federationId);
    List<UserFederation> getByUserId(Long userId, String type);
    void delete(Long userId, Long federationId);
}
