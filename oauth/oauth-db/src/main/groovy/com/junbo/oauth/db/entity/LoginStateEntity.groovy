/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.entity

import groovy.transform.CompileStatic

/**
 * Javadoc.
 */
@CompileStatic
class LoginStateEntity {
    String id
    Long userId
    Date expiredBy
    Date lastAuthDate
    String sessionId
}
