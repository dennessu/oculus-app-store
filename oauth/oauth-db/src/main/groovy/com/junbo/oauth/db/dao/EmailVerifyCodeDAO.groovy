/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.dao

import com.junbo.oauth.db.entity.EmailVerifyCodeEntity
import groovy.transform.CompileStatic

/**
 * EmailVerifyCodeDAO.
 */
@CompileStatic
interface EmailVerifyCodeDAO extends BaseDAO<EmailVerifyCodeEntity, String> {
}
