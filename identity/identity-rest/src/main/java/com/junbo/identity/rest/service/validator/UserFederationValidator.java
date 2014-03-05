/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.service.validator;

import com.junbo.identity.spec.model.user.UserFederation;

/**
 * Created by liangfu on 3/3/14.
 */
public interface UserFederationValidator {
    void validateCreate(Long userId, UserFederation userFederation);
    void validateUpdate(Long userId, Long federationId, UserFederation userFederation);
    void validateDelete(Long userId, Long federationId);
    void validateResourceAccessible(Long userId, Long federationId);
}
