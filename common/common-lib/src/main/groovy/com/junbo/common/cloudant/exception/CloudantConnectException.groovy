package com.junbo.common.cloudant.exception

import groovy.transform.CompileStatic

/**
 * CloudantConnectException.
 */
@CompileStatic
class CloudantConnectException extends RuntimeException {

    CloudantConnectException(String message) {
        super(message)
    }

    CloudantConnectException(String message, Throwable e) {
        super(message, e)
    }
}
