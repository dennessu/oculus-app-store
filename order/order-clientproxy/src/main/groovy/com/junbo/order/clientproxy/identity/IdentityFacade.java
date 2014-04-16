/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.clientproxy.identity;

import com.junbo.identity.spec.v1.model.User;
import com.junbo.langur.core.promise.Promise;

/**
 * Created by linyi on 14-2-19.
 */

public interface IdentityFacade {

    Promise<User> getUser(Long userId);

    Promise<User> createUser(User user);
}
