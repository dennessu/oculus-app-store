/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository;

import com.junbo.common.id.UserSecurityQuestionAttemptId;
import com.junbo.identity.spec.model.users.UserSecurityQuestionAttempt;
import com.junbo.identity.spec.options.list.UserSecurityQuestionAttemptListOptions;

import java.util.List;

/**
 * Created by liangfu on 3/25/14.
 */
public interface UserSecurityQuestionAttemptRepository {

    UserSecurityQuestionAttempt save(UserSecurityQuestionAttempt entity);

    UserSecurityQuestionAttempt update(UserSecurityQuestionAttempt entity);

    UserSecurityQuestionAttempt get(UserSecurityQuestionAttemptId id);

    List<UserSecurityQuestionAttempt> search(UserSecurityQuestionAttemptListOptions getOption);
}
