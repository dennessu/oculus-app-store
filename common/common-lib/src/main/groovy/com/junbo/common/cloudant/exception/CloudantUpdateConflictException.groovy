package com.junbo.common.cloudant.exception

import groovy.transform.CompileStatic

/**
 * CloudantUpdateConflictException.
 */
@CompileStatic
class CloudantUpdateConflictException extends RuntimeException {

    CloudantUpdateConflictException(String message) {
        super(message)
    }

    CloudantUpdateConflictException(String message, Throwable e) {
        super(message, e)
    }
}
