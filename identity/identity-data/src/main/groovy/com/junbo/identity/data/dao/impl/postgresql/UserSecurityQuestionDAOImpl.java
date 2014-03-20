/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl.postgresql;

import com.junbo.identity.data.dao.UserSecurityQuestionDAO;
import com.junbo.identity.data.entity.user.UserSecurityQuestionEntity;
import com.junbo.identity.spec.options.UserSecurityQuestionListOptions;
import com.junbo.sharding.annotations.SeedParam;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by liangfu on 3/17/14.
 */
@Component
public class UserSecurityQuestionDAOImpl extends BaseDaoImpl<UserSecurityQuestionEntity, Long>
        implements UserSecurityQuestionDAO {

    @Override
    public List<UserSecurityQuestionEntity> search(@SeedParam Long userId, UserSecurityQuestionListOptions getOption) {
        return null;
    }

    @Override
    public void delete(Long id) {
        UserSecurityQuestionEntity entity = (UserSecurityQuestionEntity)currentSession().
                get(UserSecurityQuestionEntity.class, id);
        currentSession().delete(entity);
    }
}
