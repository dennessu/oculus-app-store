/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.core.service.user;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserPasswordId;
import com.junbo.identity.spec.model.users.UserPassword;
import com.junbo.identity.spec.options.list.UserPasswordListOption;

import java.util.List;

/**
 * Created by liangfu on 3/24/14.
 */
public interface UserPasswordService {
    UserPassword get(UserId userId, UserPasswordId userPasswordId);
    UserPassword create(UserId userId, UserPassword userPassword);
    List<UserPassword> search(UserPasswordListOption getOptions);
}
