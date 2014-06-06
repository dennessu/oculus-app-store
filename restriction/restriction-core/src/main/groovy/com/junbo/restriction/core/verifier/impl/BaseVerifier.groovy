/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.restriction.core.verifier.impl

import com.junbo.catalog.spec.model.offer.Offer
import com.junbo.restriction.clientproxy.CatalogFacade
import com.junbo.restriction.clientproxy.IdentityFacade
import com.junbo.restriction.core.configuration.RestrictionConfiguration
import com.junbo.restriction.spec.internal.Restriction
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier

/**
 * Base Verifier.
 */
@CompileStatic
abstract class BaseVerifier {
    @Autowired
    protected RestrictionConfiguration configuration

    @Autowired
    @Qualifier('mockCatalogFacade')
    protected CatalogFacade catalogFacade

    @Autowired
    @Qualifier('mockIdentityFacade')
    protected IdentityFacade identityFacade

    abstract boolean isMatch()

    abstract String getCountry()

    protected Restriction getRestriction() {
        configuration.restrictions.find { Restriction restriction ->
            restriction.country.equalsIgnoreCase(this.country)
        }
    }

    protected List<String> getCountries(String type) {
        return configuration.restrictions.collectMany { Restriction restriction ->
            restriction.type.equalsIgnoreCase(type) ? [restriction.country] : []
        }
    }

    protected int calculateAge(Date dob) {
        Calendar calendar = Calendar.instance
        calendar.setTime(new Date())
        int currentYear = calendar.get(Calendar.YEAR)
        int currentDay = calendar.get(Calendar.DAY_OF_YEAR)

        Calendar birth = Calendar.instance
        birth.setTime(dob)
        int birthYear = birth.get(Calendar.YEAR)
        int birthDay = birth.get(Calendar.DAY_OF_YEAR)

        int age = currentYear - birthYear
        if (currentDay <= birthDay) {
            age--
        }
        return age
    }

    protected int getMaxRatingAge(List<Offer> offers) {
        // TODO: need to implement the logic later
        def defaultRatingAge = 0
        return offers == null ? defaultRatingAge : defaultRatingAge + 18
    }
}
