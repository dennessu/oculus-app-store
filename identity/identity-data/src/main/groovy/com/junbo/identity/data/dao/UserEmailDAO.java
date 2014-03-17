/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao;

import com.junbo.identity.data.entity.user.UserEmailEntity;
import com.junbo.identity.spec.model.options.UserEmailGetOption;

import java.util.List;

/**
 * Created by liangfu on 3/17/14.
 */
public interface UserEmailDAO {
    UserEmailEntity save(UserEmailEntity entity);

    UserEmailEntity update(UserEmailEntity entity);

    UserEmailEntity get(Long id);

    List<UserEmailEntity> search(UserEmailGetOption getOption);

    void delete(Long id);
}
