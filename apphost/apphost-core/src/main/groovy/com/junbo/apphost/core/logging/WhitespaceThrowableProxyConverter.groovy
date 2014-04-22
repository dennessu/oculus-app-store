package com.junbo.apphost.core.logging

import ch.qos.logback.classic.pattern.ThrowableProxyConverter
import ch.qos.logback.classic.spi.IThrowableProxy
import ch.qos.logback.core.CoreConstants
import groovy.transform.CompileStatic

/**
 * Created by kg on 4/21/2014.
 */
@CompileStatic
class WhitespaceThrowableProxyConverter extends ThrowableProxyConverter {

    @Override
    protected String throwableProxyToString(IThrowableProxy tp) {
        return CoreConstants.LINE_SEPARATOR + super.throwableProxyToString(tp) + CoreConstants.LINE_SEPARATOR
    }
}
