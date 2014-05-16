package com.junbo.identity.core.service.util

import groovy.transform.CompileStatic

/**
 * Created by liangfu on 5/15/14.
 */
@CompileStatic
interface SaltGenerator {
    String generateSalt()
}
