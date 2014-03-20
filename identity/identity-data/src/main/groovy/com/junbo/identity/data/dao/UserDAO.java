/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao;

import com.junbo.identity.data.entity.user.UserEntity;
import com.junbo.sharding.annotations.SeedParam;
/**
 * User DAO is used to fetch/update/delete/get user data from the database.
 */
public interface UserDAO extends BaseDao<UserEntity, Long> {
    void delete(@SeedParam Long userId);
}
