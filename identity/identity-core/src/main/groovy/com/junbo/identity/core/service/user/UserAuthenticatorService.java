/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.core.service.user;

import com.junbo.common.id.UserAuthenticatorId;
import com.junbo.common.id.UserId;
import com.junbo.identity.spec.options.list.UserAuthenticatorListOption;
import com.junbo.identity.spec.model.users.UserAuthenticator;
import java.util.List;

/**
 * Created by liangfu on 2/20/14.
 */
public interface UserAuthenticatorService {
    UserAuthenticator save(UserId userId, UserAuthenticator userAuthenticator);
    UserAuthenticator update(UserId userId, UserAuthenticatorId userAuthenticatorId,
                             UserAuthenticator userAuthenticator);
    UserAuthenticator patch(UserId userId, UserAuthenticatorId userAuthenticatorId,
                            UserAuthenticator userAuthenticator);
    UserAuthenticator get(UserId userId, UserAuthenticatorId userAuthenticatorId);
    List<UserAuthenticator> search(UserAuthenticatorListOption getOption);
    void delete(UserId userId, UserAuthenticatorId userAuthenticatorId);
}
