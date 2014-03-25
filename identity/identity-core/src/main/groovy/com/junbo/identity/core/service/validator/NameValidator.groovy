package com.junbo.identity.core.service.validator

import com.junbo.identity.spec.model.users.UserName

/**
 * Created by kg on 3/17/14.
 */
interface NameValidator {

    void validateName(UserName name)
}
