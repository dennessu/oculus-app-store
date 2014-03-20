/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl.postgresql;
import com.junbo.identity.data.dao.UserDeviceDAO;
import com.junbo.identity.data.entity.user.UserDeviceEntity;
import com.junbo.identity.spec.options.UserDeviceListOptions;
import com.junbo.sharding.annotations.SeedParam;

import java.util.List;

/**
 * Implementation for UserDeviceDAO.
 */
public class UserDeviceDAOImpl extends BaseDaoImpl<UserDeviceEntity, Long> implements UserDeviceDAO {

    @Override
    public List<UserDeviceEntity> search(@SeedParam Long userId, UserDeviceListOptions getOption) {
        return null;
    }

    @Override
    public  void delete(@SeedParam Long id) {

    }
}
