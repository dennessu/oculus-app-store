/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.clientproxy.facebook

import groovy.transform.CompileStatic

/**
 * FacebookException.
 */
@CompileStatic
class FacebookException extends RuntimeException {
    FacebookError error
    FacebookException(FacebookError error) {
        super(error.error.message)
        this.error = error
    }

    FacebookException(FacebookError error, Throwable e) {
        super(error.error.message, e)
        this.error = error
    }

    com.junbo.common.error.Error commonError() {
        return new com.junbo.common.error.Error(
                code: '20071',
                description: error.error.message,
                field: 'facebookAuth'
        )
    }
}
