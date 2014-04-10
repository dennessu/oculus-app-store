package com.junbo.identity.core.service.validator

import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.model.UserPii


/**
 * Created by kg on 3/17/14.
 */
interface DisplayNameValidator {

    Integer getDisplayNameType(User user, UserPii userPii)

    String getDisplayName(User user, UserPii userPii)

}
