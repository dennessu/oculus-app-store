/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.service.validator.impl;

import com.junbo.identity.data.dao.UserDAO;
import com.junbo.identity.data.entity.user.UserStatus;
import com.junbo.identity.spec.error.AppErrors;
import com.junbo.identity.spec.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by liangfu on 3/3/14.
 */
@Component
public class CommonValidator {
    @Autowired
    protected UserDAO userDAO;

    protected void checkUserValid(Long userId) {
        User user = userDAO.getUser(userId);
        if(user == null) {
            throw AppErrors.INSTANCE.notExistingUser("userId = " + userId.toString()).exception();
        }
        if(user.getStatus().equals(UserStatus.BANNED.toString())
                || user.getStatus().equals(UserStatus.DELETED.toString())) {
            throw AppErrors.INSTANCE.userStatusError("userId = " + userId.toString()).exception();
        }
    }
}
