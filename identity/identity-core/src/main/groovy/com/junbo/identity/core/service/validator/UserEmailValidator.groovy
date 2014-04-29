package com.junbo.identity.core.service.validator

import com.junbo.identity.spec.v1.model.Email
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 3/31/14.
 */
@CompileStatic
interface UserEmailValidator {
    void validate(Email email)
}
