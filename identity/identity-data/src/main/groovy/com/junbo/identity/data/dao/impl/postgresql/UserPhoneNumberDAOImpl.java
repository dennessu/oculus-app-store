/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl.postgresql;

import com.junbo.identity.data.dao.UserPhoneNumberDAO;
import com.junbo.identity.data.entity.user.UserPhoneNumberEntity;
import com.junbo.identity.spec.options.UserPhoneNumberListOptions;
import com.junbo.sharding.annotations.SeedParam;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by liangfu on 3/17/14.
 */
@Component
public class UserPhoneNumberDAOImpl extends BaseDaoImpl<UserPhoneNumberEntity, Long> implements UserPhoneNumberDAO {

    @Override
    public List<UserPhoneNumberEntity> search(@SeedParam Long userId, UserPhoneNumberListOptions getOption) {
        return null;
    }

    @Override
    public void delete(Long id) {
        UserPhoneNumberEntity entity =
                (UserPhoneNumberEntity)currentSession().get(UserPhoneNumberEntity.class, id);
        currentSession().delete(entity);
    }
}
