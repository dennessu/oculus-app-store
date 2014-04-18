/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.core.validator

import com.junbo.email.spec.model.Email
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.model.UserPii

/**
 * Interface of Email.
 */
interface EmailValidator {
    void validateCreate(Email email)

    void validateUpdate(Email email)

    void validateDelete(Long id)

    void validateReplacements(Email email)

    void validateUser(User user)

    void validateUserPii(UserPii userPii)
}
