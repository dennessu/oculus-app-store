/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.clientproxy.facebook

import groovy.transform.CompileStatic

/**
 * FacebookError.
 */
@CompileStatic
class FacebookError {
    Error error
    static class Error {
        String message
        String type
        int code
    }
}
