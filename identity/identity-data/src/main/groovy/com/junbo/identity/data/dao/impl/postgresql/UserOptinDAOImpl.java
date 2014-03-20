/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl.postgresql;
import com.junbo.identity.data.dao.UserOptinDAO;
import com.junbo.identity.data.entity.user.UserOptinEntity;
import com.junbo.identity.spec.options.UserOptinListOptions;
import com.junbo.sharding.annotations.SeedParam;

import java.util.List;

/**
 * Implementation for UserOptinDAO.
 */
public class UserOptinDAOImpl extends BaseDaoImpl<UserOptinEntity, Long> implements UserOptinDAO {

    @Override
    public List<UserOptinEntity> search(Long userId, UserOptinListOptions options) {
        return null;
    }

    @Override
    public void delete(@SeedParam Long id) {

    }
}
