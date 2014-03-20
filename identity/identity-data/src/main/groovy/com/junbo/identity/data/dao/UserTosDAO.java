/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao;

import com.junbo.identity.data.entity.user.UserTosEntity;
import com.junbo.identity.spec.options.UserTosListOptions;
import com.junbo.sharding.annotations.SeedParam;

import java.util.List;

/**
 * User tos acceptance DAO is used to fetch/update/delete/get user tos Acceptance(eg, legal) from the database.
 */
public interface UserTosDAO extends BaseDao<UserTosEntity, Long> {

    List<UserTosEntity> search(@SeedParam Long userId, UserTosListOptions getOption);

    void delete(@SeedParam Long id);
}
