/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.crypto.rest.resource

import com.junbo.common.id.UserId
import com.junbo.crypto.spec.resource.UserCryptoResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by liangfu on 5/6/14.
 */
@CompileStatic
@Transactional
class UserCryptoResourceImpl implements UserCryptoResource {

    @Override
    Promise<Void> generateUserEncryptKey(UserId userId) {
        return null
    }
}
