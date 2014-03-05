package com.junbo.langur.core.webflow

import groovy.transform.CompileStatic

/**
 * Created by kg on 2/26/14.
 */
@CompileStatic
class FlowException extends RuntimeException {

    FlowException() {
    }

    FlowException(String s) {
        super(s)
    }

    FlowException(String s, Throwable throwable) {
        super(s, throwable)
    }

    FlowException(Throwable throwable) {
        super(throwable)
    }

}
