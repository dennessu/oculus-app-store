/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository;

import com.junbo.common.id.UserEmailId;
import com.junbo.identity.spec.model.options.UserEmailGetOption;
import com.junbo.identity.spec.model.users.UserEmail;

import java.util.List;

/**
 * Created by liangfu on 3/17/14.
 */
public interface UserEmailRepository {
    UserEmail save(UserEmail entity);

    UserEmail update(UserEmail entity);

    UserEmail get(UserEmailId id);

    List<UserEmail> search(UserEmailGetOption getOption);

    void delete(UserEmailId id);
}
