package com.junbo.common.cloudant.exception

import groovy.transform.CompileStatic

/**
 * CloudantException.
 */
@CompileStatic
class CloudantException extends RuntimeException {
    CloudantException(String message) {
        super(message)
    }

    CloudantException(String message, Throwable e) {
        super(message, e)
    }
}
