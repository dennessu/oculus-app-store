/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao;

import com.junbo.identity.data.entity.user.UserGroupEntity;
import com.junbo.identity.spec.options.list.UserGroupListOptions;
import com.junbo.sharding.annotations.SeedParam;

import java.util.List;

/**
 * Created by liangfu on 3/17/14.
 */
public interface UserGroupDAO {
    UserGroupEntity save(UserGroupEntity entity);

    UserGroupEntity update(UserGroupEntity entity);

    UserGroupEntity get(@SeedParam Long id);

    List<UserGroupEntity> search(@SeedParam Long userId, UserGroupListOptions getOption);

    void delete(@SeedParam Long id);
}
