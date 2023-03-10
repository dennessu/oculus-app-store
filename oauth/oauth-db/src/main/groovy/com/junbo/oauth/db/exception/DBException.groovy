/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.exception

import groovy.transform.CompileStatic

/**
 * DBException.
 */
@CompileStatic
class DBException extends RuntimeException {
    DBException(String message) {
        super(message)
    }

    DBException(String message, Throwable e) {
        super(message, e)
    }
}
