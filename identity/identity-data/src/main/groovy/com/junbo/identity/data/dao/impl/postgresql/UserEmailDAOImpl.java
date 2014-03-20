/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl.postgresql;

import com.junbo.identity.data.dao.UserEmailDAO;
import com.junbo.identity.data.entity.user.UserEmailEntity;
import com.junbo.identity.spec.options.UserEmailListOptions;
import com.junbo.sharding.annotations.SeedParam;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by liangfu on 3/17/14.
 */
@Component
public class UserEmailDAOImpl extends BaseDaoImpl<UserEmailEntity, Long> implements UserEmailDAO {

    @Override
    public void delete(@SeedParam Long id) {

    }

    @Override
    public List<UserEmailEntity> search(UserEmailListOptions options) {
        return null;
    }
}
