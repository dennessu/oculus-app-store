package com.junbo.identity.core.service.validator

import com.junbo.identity.spec.model.users.User

/**
 * Created by kg on 3/17/14.
 */
interface DisplayNameValidator {

    Integer getDisplayNameType(User user)

    String getDisplayName(User user)

}
