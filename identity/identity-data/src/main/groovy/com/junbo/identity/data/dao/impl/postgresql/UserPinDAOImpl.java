/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl.postgresql;

import com.junbo.identity.data.dao.UserPinDAO;
import com.junbo.identity.data.entity.user.UserPinEntity;
import com.junbo.identity.spec.options.UserPinListOptions;
import com.junbo.sharding.annotations.SeedParam;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by liangfu on 3/16/14.
 */
@Component
public class UserPinDAOImpl extends BaseDaoImpl<UserPinEntity, Long> implements UserPinDAO {

    @Override
    public List<UserPinEntity> search(@SeedParam Long userId, UserPinListOptions getOption) {
        return null;
    }

    @Override
    public void delete(Long id) {
        UserPinEntity entity = (UserPinEntity)currentSession().get(UserPinEntity.class, id);
        currentSession().delete(entity);
    }
}
