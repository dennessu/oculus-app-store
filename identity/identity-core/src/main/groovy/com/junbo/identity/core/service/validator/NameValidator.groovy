package com.junbo.identity.core.service.validator

import com.junbo.identity.spec.v1.model.UserName
import groovy.transform.CompileStatic

/**
 * Created by kg on 3/17/14.
 */
@CompileStatic
interface NameValidator {
    void validateName(UserName name)
}
