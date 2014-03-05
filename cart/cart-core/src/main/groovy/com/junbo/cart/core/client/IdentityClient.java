/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.cart.core.client;

import com.junbo.common.id.UserId;
import com.junbo.identity.spec.model.user.User;
import com.junbo.langur.core.promise.Promise;

/**
 * Created by fzhang@wan-san.com on 14-2-18.
 */
public interface IdentityClient {
    Promise<User> getUser(UserId userId);
}
