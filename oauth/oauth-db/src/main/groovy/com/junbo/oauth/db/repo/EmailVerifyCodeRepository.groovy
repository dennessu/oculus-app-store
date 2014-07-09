/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.repo

import com.junbo.oauth.spec.model.EmailVerifyCode
import groovy.transform.CompileStatic

/**
 * EmailVerifyCodeRepository.
 */
@CompileStatic
interface EmailVerifyCodeRepository {
    EmailVerifyCode getAndRemove(String code)

    void save(EmailVerifyCode emailVerifyCode)

    void removeByUserIdEmail(Long userId, String email)
}
