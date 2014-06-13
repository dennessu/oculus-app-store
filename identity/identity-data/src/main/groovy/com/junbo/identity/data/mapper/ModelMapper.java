/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.identity.data.mapper;

import com.junbo.identity.spec.model.users.UserPassword;
import com.junbo.identity.spec.model.users.UserPin;
import com.junbo.identity.spec.v1.model.UserCredential;
import com.junbo.oom.core.Mapper;
import com.junbo.oom.core.MappingContext;

/**
 * Model Mapper for wrap entity to list, vice versa.
 */
@Mapper(uses = {
        CommonMapper.class
})
public interface ModelMapper {
    UserPassword credentialToPassword(UserCredential userCredential, MappingContext context);
    UserCredential passwordToCredential(UserPassword password, MappingContext context);

    UserPin credentialToPin(UserCredential userCredential, MappingContext context);
    UserCredential pinToCredential(UserPin userPin, MappingContext context);
}
