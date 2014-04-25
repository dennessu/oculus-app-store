package com.junbo.identity.core.service.validator

import com.sun.org.apache.xalan.internal.xsltc.cmdline.Compile
import groovy.transform.CompileStatic

/**
 * Created by kg on 3/18/14.
 */
@CompileStatic
interface TimezoneValidator {

    boolean isValidTimezone(String timezone)

    String getDefaultTimezone()
}
