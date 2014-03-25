/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao;

import com.junbo.identity.data.entity.user.UserSecurityQuestionAttemptEntity;
import com.junbo.identity.spec.options.list.UserSecurityQuestionAttemptListOptions;
import com.junbo.sharding.annotations.SeedParam;

import java.util.List;

/**
 * Created by liangfu on 3/25/14.
 */
public interface UserSecurityQuestionAttemptDAO {
    UserSecurityQuestionAttemptEntity save(UserSecurityQuestionAttemptEntity entity);

    UserSecurityQuestionAttemptEntity update(UserSecurityQuestionAttemptEntity entity);

    UserSecurityQuestionAttemptEntity get(@SeedParam Long id);

    List<UserSecurityQuestionAttemptEntity> search(@SeedParam Long userId,
                                                   UserSecurityQuestionAttemptListOptions getOption);

}
