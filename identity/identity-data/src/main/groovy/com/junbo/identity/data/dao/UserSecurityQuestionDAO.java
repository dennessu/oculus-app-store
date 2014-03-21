/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao;

import com.junbo.identity.data.entity.user.UserSecurityQuestionEntity;
import com.junbo.identity.spec.options.list.UserSecurityQuestionListOption;
import com.junbo.sharding.annotations.SeedParam;

import java.util.List;

/**
 * Created by liangfu on 3/17/14.
 */
public interface UserSecurityQuestionDAO {
    UserSecurityQuestionEntity save(UserSecurityQuestionEntity entity);

    UserSecurityQuestionEntity update(UserSecurityQuestionEntity entity);

    UserSecurityQuestionEntity get(@SeedParam Long id);

    List<UserSecurityQuestionEntity> search(@SeedParam Long userId, UserSecurityQuestionListOption getOption);

    void delete(@SeedParam Long id);
}
