/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.core.service.user;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserOptinId;
import com.junbo.identity.spec.model.users.UserOptin;
import com.junbo.identity.spec.options.list.UserOptinListOptions;

import java.util.List;

/**
 * Created by liangfu on 2/20/14.
 */
public interface UserOptinService {
    UserOptin save(UserId userId, UserOptin userOptIn);
    UserOptin update(UserId userId, UserOptinId userOptInId, UserOptin userOptIn);
    UserOptin get(UserId userId, UserOptinId userOptInId);
    List<UserOptin> search(UserOptinListOptions listOption);
    void delete(UserId userId, UserOptinId userOptInId);
}
