/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.clientproxy.google

import groovy.transform.CompileStatic

/**
 * GoogleException.
 */
@CompileStatic
class GoogleException extends RuntimeException {
    GoogleError error
    GoogleException(GoogleError error) {
        super(error.message)
        this.error = error
    }

    GoogleException(GoogleError error, Throwable e) {
        super(error.message, e)
        this.error = error
    }

    com.junbo.common.error.Error commonError() {
        return new com.junbo.common.error.Error(
                code: '20073',
                description: error.message,
                field: 'googleAuth'
        )
    }
}
