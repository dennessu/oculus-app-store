package com.junbo.identity.core.service.validator

/**
 * Created by kg on 3/17/14.
 */
interface UsernameValidator {

    void validateUsername(String username)

    // todo:    Should remove out of this interface
    String normalizeUsername(String username)
}
