/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao;

import com.junbo.identity.data.entity.user.UserPinEntity;
import com.junbo.identity.spec.options.list.UserPinListOptions;
import com.junbo.sharding.annotations.SeedParam;

import java.util.List;

/**
 * Created by liangfu on 3/16/14.
 */
public interface UserPinDAO {

    UserPinEntity save(UserPinEntity entity);

    UserPinEntity update(UserPinEntity entity);

    UserPinEntity get(@SeedParam Long id);

    List<UserPinEntity> search(@SeedParam Long userId, UserPinListOptions getOption);

    void delete(@SeedParam Long id);
}
