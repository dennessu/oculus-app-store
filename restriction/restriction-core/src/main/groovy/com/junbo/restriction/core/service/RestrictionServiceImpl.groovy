package com.junbo.restriction.core.service

import com.junbo.langur.core.promise.Promise
import com.junbo.restriction.core.RestrictionService
import com.junbo.restriction.core.configuration.RestrictionConfiguration
import com.junbo.restriction.core.validator.RestrictionValidator
import com.junbo.restriction.spec.model.AgeCheck
import com.junbo.restriction.spec.model.Status
import org.springframework.beans.factory.annotation.Autowired

/**
 * Impl of RestrictionService.
 */
class RestrictionServiceImpl implements RestrictionService {

    @Autowired
    private RestrictionValidator validator

    @Autowired
    private RestrictionConfiguration configuration

    Promise<AgeCheck> getAgeCheck(AgeCheck ageCheck) {
        validator.validate(ageCheck)
        if (supportCountry(ageCheck.country)) {
            ageCheck.status = Status.PASSED
        }
        return Promise.pure(ageCheck)
    }

    private boolean supportCountry(String country) {
        def countries = configuration.restrictions.collect { it.country.toLowerCase() }
        return countries.contains(country.toLowerCase())
    }
}
