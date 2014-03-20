/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl.postgresql;
import com.junbo.identity.data.dao.UserAuthenticatorDAO;
import com.junbo.identity.data.entity.user.UserAuthenticatorEntity;
import com.junbo.identity.spec.options.UserAuthenticatorListOptions;
import com.junbo.sharding.annotations.SeedParam;

import java.util.List;

/**
 * Implementation for UserAuthenticatorDAO.
 */
public class UserAuthenticatorDAOImpl extends BaseDaoImpl<UserAuthenticatorEntity, Long>
        implements UserAuthenticatorDAO {

    @Override
    public List<UserAuthenticatorEntity> search(@SeedParam Long userId, UserAuthenticatorListOptions options) {
        return null;
    }

    @Override
    public void delete(@SeedParam Long id) {

    }
}

