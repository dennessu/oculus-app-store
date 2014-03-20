/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao;

import com.junbo.identity.data.entity.user.UserPasswordEntity;
import com.junbo.identity.spec.options.UserPasswordListOptions;
import com.junbo.sharding.annotations.SeedParam;

import java.util.List;

/**
 * Created by liangfu on 3/16/14.
 */
public interface UserPasswordDAO extends BaseDao<UserPasswordEntity, Long> {
    List<UserPasswordEntity> search(@SeedParam Long userId, UserPasswordListOptions getOption);
    void delete(@SeedParam Long id);
}
