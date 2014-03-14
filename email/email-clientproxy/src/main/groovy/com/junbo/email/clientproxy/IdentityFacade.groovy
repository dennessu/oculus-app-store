/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.clientproxy

import com.junbo.langur.core.promise.Promise
import com.junbo.identity.spec.model.user.User
/**
 * Interface of Identity Facade.
 */
interface IdentityFacade {
    Promise<User> getUser(Long userId)
}