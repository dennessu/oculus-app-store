package com.junbo.restriction.core

import com.junbo.langur.core.promise.Promise
import com.junbo.restriction.spec.model.AgeCheck

/**
 * Interface of RestrictionService.
 */
interface RestrictionService {
    Promise<AgeCheck> getAgeCheck(AgeCheck ageCheck)
}