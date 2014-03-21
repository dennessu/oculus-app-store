/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository;

import com.junbo.common.id.UserEmailId;
import com.junbo.identity.spec.options.list.UserEmailListOption;
import com.junbo.identity.spec.model.users.UserEmail;

import java.util.List;

/**
 * Created by liangfu on 3/17/14.
 */
public interface UserEmailRepository {
    UserEmail save(UserEmail entity);

    UserEmail update(UserEmail entity);

    UserEmail get(UserEmailId id);

    List<UserEmail> search(UserEmailListOption getOption);

    UserEmail findByUserEmail(String value);

    void delete(UserEmailId id);
}
