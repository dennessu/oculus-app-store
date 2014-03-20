/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao;

import com.junbo.identity.data.entity.user.UserEmailEntity;
import com.junbo.identity.spec.options.UserEmailListOptions;
import com.junbo.sharding.annotations.SeedParam;

import java.util.List;

/**
 * Created by liangfu on 3/17/14.
 */
public interface UserEmailDAO extends BaseDao<UserEmailEntity, Long> {

    void delete(@SeedParam Long id);

    // Todo:    Need to build reverse lookup table
    List<UserEmailEntity> search(UserEmailListOptions options);
}
