/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao;

import com.junbo.identity.data.entity.group.GroupUserEntity;
import com.junbo.sharding.annotations.SeedParam;

import java.util.List;

/**
 * Created by liangfu on 3/21/14.
 */
public interface GroupUserDAO {

    GroupUserEntity save(GroupUserEntity entity);

    GroupUserEntity update(GroupUserEntity entity);

    GroupUserEntity findByGroupIdAndUserId(@SeedParam Long groupId, Long userId);

    List<GroupUserEntity> findByGroupId(@SeedParam Long groupId);

    GroupUserEntity get(@SeedParam Long id);

    void delete(@SeedParam Long id);
}
