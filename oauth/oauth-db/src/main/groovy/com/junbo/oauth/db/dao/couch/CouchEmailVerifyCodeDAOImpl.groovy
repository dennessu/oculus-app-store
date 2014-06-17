/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.dao.couch

import com.junbo.oauth.db.dao.EmailVerifyCodeDAO
import com.junbo.oauth.db.entity.EmailVerifyCodeEntity
import groovy.transform.CompileStatic

/**
 * CouchEmailVerifyCodeDAOImpl.
 */
@CompileStatic
class CouchEmailVerifyCodeDAOImpl extends CouchBaseDAO<EmailVerifyCodeEntity> implements EmailVerifyCodeDAO {
}
