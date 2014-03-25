/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.core.service.user;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserTosId;
import com.junbo.identity.spec.model.users.UserTos;
import com.junbo.identity.spec.options.list.UserTosListOption;

import java.util.List;

/**
 * Created by liangfu on 2/20/14.
 */
public interface UserTosService {
    UserTos save(UserId userId, UserTos userTosAcceptance);
    UserTos update(UserId userId, UserTosId userTosId, UserTos userTosAcceptance);
    UserTos get(UserId userId, UserTosId userTosId);
    List<UserTos> search(UserTosListOption listOption);
    void delete(UserId userId, UserTosId userTosId);
}
