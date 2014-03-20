/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao;

import com.junbo.identity.data.entity.user.UserNameEntity;
import com.junbo.sharding.annotations.SeedParam;

/**
 * Created by liangfu on 3/18/14.
 */
public interface UserNameDAO extends BaseDao<UserNameEntity, Long> {
    void delete(@SeedParam Long id);
    UserNameEntity findByUserId(@SeedParam Long userId);
}
