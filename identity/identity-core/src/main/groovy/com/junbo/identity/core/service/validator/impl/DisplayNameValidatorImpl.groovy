package com.junbo.identity.core.service.validator.impl

import com.junbo.identity.core.service.validator.DisplayNameValidator
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.UserName
import groovy.transform.CompileStatic

/**
 * Created by kg on 3/17/14.
 */
@CompileStatic
class DisplayNameValidatorImpl implements DisplayNameValidator {
    // todo:    Need to do validation according to marshall's requirement

    @Override
    void validate(UserName name) {

    }

}
