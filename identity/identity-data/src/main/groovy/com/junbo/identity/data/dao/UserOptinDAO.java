/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao;

import com.junbo.identity.data.entity.user.UserOptinEntity;
import com.junbo.identity.spec.options.UserOptinListOptions;
import com.junbo.sharding.annotations.SeedParam;

import java.util.List;

/**
 * Created by xiali on 3/18/14.
 */
public interface UserOptinDAO extends BaseDao<UserOptinEntity, Long> {
    List<UserOptinEntity> search(Long userId, UserOptinListOptions options);
    void delete(@SeedParam Long id);
}
