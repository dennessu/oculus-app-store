/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl.postgresql;
import com.junbo.identity.data.dao.UserTosDAO;
import com.junbo.identity.data.entity.user.UserTosEntity;
import com.junbo.identity.spec.options.UserTosListOptions;
import com.junbo.sharding.annotations.SeedParam;

import java.util.List;

/**
 * Implementation for User Tos Acceptance DAO interface.
 */
public class UserTosDAOImpl extends BaseDaoImpl<UserTosEntity, Long> implements UserTosDAO {

    @Override
    public List<UserTosEntity> search(@SeedParam Long userId, UserTosListOptions getOption) {
        return null;
    }

    @Override
    public void delete(@SeedParam Long id) {

    }
}
