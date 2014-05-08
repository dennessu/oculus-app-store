/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.crypto.rest.resource

import com.junbo.common.id.UserId
import com.junbo.crypto.spec.resource.CryptoResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.transaction.annotation.Transactional

/**
 * Created by liangfu on 5/6/14.
 */
@CompileStatic
@Transactional
class CryptoResourceImpl implements CryptoResource {

    @Override
    Promise<String> encrypt(UserId userId, Object obj) {
        return null
    }

    @Override
    Promise<Object> decrypt(UserId userId, String encrypted) {
        return null
    }
}
