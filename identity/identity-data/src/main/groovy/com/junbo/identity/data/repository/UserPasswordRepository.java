/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository;

import com.junbo.common.id.UserPasswordId;
import com.junbo.identity.spec.model.users.UserPassword;
import com.junbo.identity.spec.options.UserPasswordListOptions;

import java.util.List;

/**
 * Created by liangfu on 3/16/14.
 */
public interface UserPasswordRepository {
    UserPassword save(UserPassword entity);

    UserPassword update(UserPassword entity);

    UserPassword get(UserPasswordId id);

    List<UserPassword> search(UserPasswordListOptions getOption);

    void delete(UserPasswordId id);
}
