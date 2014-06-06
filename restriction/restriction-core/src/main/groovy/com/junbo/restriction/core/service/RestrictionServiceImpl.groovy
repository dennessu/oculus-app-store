/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.restriction.core.service

import com.junbo.langur.core.promise.Promise
import com.junbo.restriction.core.RestrictionService
import com.junbo.restriction.core.configuration.RestrictionConfiguration
import com.junbo.restriction.core.validator.RestrictionValidator
import com.junbo.restriction.core.verifier.Verifier
import com.junbo.restriction.spec.internal.Restriction
import com.junbo.restriction.spec.model.AgeCheck
import com.junbo.restriction.spec.model.Status
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Required

/**
 * Impl of RestrictionService.
 */
@CompileStatic
class RestrictionServiceImpl implements RestrictionService {

    @Autowired
    private RestrictionValidator validator

    @Autowired
    private RestrictionConfiguration configuration

    private List<Verifier> verifiers

    private boolean enabled

    @Required
    void setVerifiers(List<Verifier> verifiers) {
        this.verifiers = verifiers
    }

    @Required
    void setEnabled(boolean enabled) {
        this.enabled = enabled
    }

    Promise<AgeCheck> getAgeCheck(AgeCheck ageCheck) {
        validator.validate(ageCheck)
        ageCheck.setStatus(Status.PASSED)
        if (!(enabled && supportCountry(ageCheck.country))) {
            return Promise.pure(ageCheck)
        }
        for (Verifier verifier : verifiers) {
            verifier.setCountry(ageCheck.country)
            if (verifier.isMatch()) {
                return verifier.verify(ageCheck)
            }
        }
        return Promise.pure(ageCheck)
    }

    private boolean supportCountry(String country) {
        return  configuration.restrictions.any { Restriction it ->
            it.country.equalsIgnoreCase(country)
        }
    }
}
