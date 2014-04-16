/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.restriction.core.validator.impl

import com.junbo.restriction.core.validator.RestrictionValidator
import com.junbo.restriction.spec.error.AppErrors
import com.junbo.restriction.spec.model.AgeCheck

/**
 * Impl of RestrictionValidator.
 */
class RestrictionValidatorImpl implements RestrictionValidator {
    void validate(AgeCheck ageCheck) {
        if (ageCheck == null) {
            throw AppErrors.INSTANCE.invalidPayload().exception()
        }
        if (ageCheck.status != null) {
            throw AppErrors.INSTANCE.unnecessaryField(ageCheck.status).exception()
        }
        if (ageCheck.dob == null && ageCheck.userId == null) {
            throw AppErrors.INSTANCE.missingField('dob or userId').exception()
        }
        if (ageCheck.country == null) {
            throw AppErrors.INSTANCE.missingField('country').exception()
        }
    }
}
