/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.service.validator.impl;

import com.junbo.common.id.UserId;
import com.junbo.identity.data.repository.UserRepository;
import com.junbo.identity.spec.error.AppErrors;
import com.junbo.identity.spec.model.users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by liangfu on 3/3/14.
 */
@Component
public class CommonValidator {
    @Autowired
    protected UserRepository userRepository;

    protected void checkUserValid(Long userId) {
        User user = userRepository.get(new UserId(userId));
        if(user == null) {
            throw AppErrors.INSTANCE.notExistingUser("userId = " + userId.toString()).exception();
        }
    }

    protected void checkFieldAccess(Class cls, String preFix) {

    }
}
