/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.core.service.user;

import com.junbo.identity.spec.options.list.UserAuthenticatorListOption;
import com.junbo.identity.spec.model.users.UserAuthenticator;
import java.util.List;

/**
 * Created by liangfu on 2/20/14.
 */
public interface UserAuthenticatorService {
    UserAuthenticator save(Long userId, UserAuthenticator userFederation);
    UserAuthenticator update(Long userId, Long federationId, UserAuthenticator userFederation);
    UserAuthenticator get(Long userId, Long federationId);
    List<UserAuthenticator> search(UserAuthenticatorListOption getOption);
    void delete(Long userId, Long federationId);
}
