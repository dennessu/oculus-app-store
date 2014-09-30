package com.junbo.langur.core.webflow

/**
 * Created by Zhanxin on 9/30/2014.
 */
class IpViolationException extends FlowException {
    IpViolationException() {
    }

    IpViolationException(String s) {
        super(s)
    }

    IpViolationException(String s, Throwable throwable) {
        super(s, throwable)
    }

    IpViolationException(Throwable throwable) {
        super(throwable)
    }
}
