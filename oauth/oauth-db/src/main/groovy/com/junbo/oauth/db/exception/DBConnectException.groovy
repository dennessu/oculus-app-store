/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.exception

import groovy.transform.CompileStatic

/**
 * DBConnectException.
 */
@CompileStatic
class DBConnectException extends RuntimeException {

    DBConnectException(String message) {
        super(message)
    }

    DBConnectException(String message, Throwable e) {
        super(message, e)
    }
}
