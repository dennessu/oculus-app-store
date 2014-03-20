/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl.postgresql;

import com.junbo.identity.data.dao.UserGroupDAO;
import com.junbo.identity.data.entity.user.UserGroupEntity;
import com.junbo.identity.spec.options.UserGroupListOptions;
import com.junbo.sharding.annotations.SeedParam;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by liangfu on 3/17/14.
 */
@Component
public class UserGroupDAOImpl extends BaseDaoImpl<UserGroupEntity, Long> implements UserGroupDAO {

    @Override
    public List<UserGroupEntity> search(@SeedParam Long userId, UserGroupListOptions options) {
        return null;
    }

    @Override
    public void delete(@SeedParam Long id) {

    }
}
