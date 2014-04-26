package com.junbo.identity.core.service.validator

import com.junbo.identity.spec.v1.model.UserGovernmentID
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 4/26/14.
 */
@CompileStatic
interface GovernmentIDValidator {

    void validate(UserGovernmentID userGovernmentID)
}