/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.restriction.clientproxy

import com.junbo.identity.spec.v1.model.User
import com.junbo.langur.core.promise.Promise
/**
 * Interface of Identity Facade.
 */
interface IdentityFacade {
    Promise<User> getUser(Long userId)
}