package com.junbo.identity.core.service.validator

import groovy.transform.CompileStatic

/**
 * Created by liangfu on 4/10/14.
 */
@CompileStatic
interface UserPiiValidator {
    /*
    Promise<UserPii> validateForGet(UserPiiId userPiiId)
    Promise<Void> validateForSearch(UserPiiListOptions options)
    Promise<Void> validateForCreate(UserPii userPii)
    Promise<Void> validateForUpdate(UserPiiId userPiiId, UserPii userPii, UserPii oldUserPii)
    */
    void test()
}
