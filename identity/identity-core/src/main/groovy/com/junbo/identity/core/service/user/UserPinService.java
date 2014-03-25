/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.core.service.user;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserPinId;
import com.junbo.identity.spec.model.users.UserPin;
import com.junbo.identity.spec.options.list.UserPinListOptions;

import java.util.List;

/**
 * Created by liangfu on 3/24/14.
 */
public interface UserPinService {
    UserPin get(UserId userId, UserPinId userPinId);
    UserPin create(UserId userId, UserPin userPin);
    List<UserPin> search(UserPinListOptions getOptions);
}
