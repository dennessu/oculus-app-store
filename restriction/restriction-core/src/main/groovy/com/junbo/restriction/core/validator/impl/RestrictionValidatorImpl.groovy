/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.restriction.core.validator.impl

import com.junbo.common.error.AppCommonErrors
import com.junbo.restriction.core.validator.RestrictionValidator
import com.junbo.restriction.spec.model.AgeCheck
import groovy.transform.CompileStatic

/**
 * Impl of RestrictionValidator.
 */
@CompileStatic
class RestrictionValidatorImpl implements RestrictionValidator {
    void validate(AgeCheck ageCheck) {
        if (ageCheck == null) {
            throw AppCommonErrors.INSTANCE.parameterInvalid("ageCheck").exception()
        }
        if (ageCheck.status != null) {
            throw AppCommonErrors.INSTANCE.fieldMustBeNull("ageCheck.status").exception()
        }
        if (ageCheck.dob == null && ageCheck.userId == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('dob or userId').exception()
        }
        if (ageCheck.country == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('country').exception()
        }
    }
}
