/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao;

import com.junbo.identity.data.entity.user.UserAuthenticatorEntity;
import com.junbo.identity.spec.options.UserAuthenticatorListOptions;
import com.junbo.sharding.annotations.SeedParam;

import java.util.List;

/**
 * User Federation DAO is used to fetch/update/delete/get user.
 * federation data(such as google account, facebook account) from the database
 */
public interface UserAuthenticatorDAO extends BaseDao<UserAuthenticatorEntity, Long> {
    // todo: This need to be done by reverse index
    List<UserAuthenticatorEntity> search(@SeedParam Long userId, UserAuthenticatorListOptions options);

    void delete(@SeedParam Long id);
}
