package com.junbo.identity.core.service.validator

import groovy.transform.CompileStatic

/**
 * Created by kg on 3/17/14.
 */
@CompileStatic
interface UsernameValidator {

    void validateUsername(String username)
}
