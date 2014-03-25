/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.core.service.user;

import com.junbo.common.id.UserEmailId;
import com.junbo.common.id.UserId;
import com.junbo.identity.spec.model.users.UserEmail;
import com.junbo.identity.spec.options.list.UserEmailListOptions;

import java.util.List;

/**
 * Created by liangfu on 3/25/14.
 */
public interface UserEmailService {
    UserEmail save(UserId userId, UserEmail userEmail);
    UserEmail update(UserId userId, UserEmailId userEmailId, UserEmail userEmail);
    UserEmail patch(UserId userId, UserEmailId userEmailId, UserEmail userEmail);
    UserEmail get(UserId userId, UserEmailId userEmailId);
    List<UserEmail> search(UserEmailListOptions getOption);
    void delete(UserId userId, UserEmailId userEmailId);
}
