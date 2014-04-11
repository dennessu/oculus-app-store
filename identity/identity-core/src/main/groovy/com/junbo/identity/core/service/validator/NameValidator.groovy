package com.junbo.identity.core.service.validator

import com.junbo.identity.spec.v1.model.UserName

/**
 * Created by kg on 3/17/14.
 */
interface NameValidator {
    void validateName(UserName name)
}
