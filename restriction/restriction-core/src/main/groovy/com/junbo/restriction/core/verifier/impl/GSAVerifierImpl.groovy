/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.restriction.core.verifier.impl

import com.junbo.catalog.spec.model.offer.Offer
import com.junbo.langur.core.promise.Promise
import com.junbo.restriction.core.verifier.Verifier
import com.junbo.restriction.spec.model.AgeCheck
import com.junbo.restriction.spec.model.Status
import groovy.transform.CompileStatic

/**
 * Created by Wei on 4/19/14.
 */
@CompileStatic
class GSAVerifierImpl extends BaseVerifier implements Verifier {
    private final static String TIME_ZONE_OF_CET = 'CET'
    private final static String TYPE = 'GSA'

    private String country

    void setCountry(String country) {
        this.country = country
    }

    String getCountry() {
        return country
    }

    boolean isMatch() {
        return super.getCountries(TYPE).any { String countries ->
                countries.equalsIgnoreCase(this.country)
            }
    }

    Promise<AgeCheck> verify(AgeCheck ageCheck) {
        if (ignoreTimes()) {
            ageCheck.setStatus(Status.PASSED)
            return Promise.pure(ageCheck)
        }
        return catalogFacade.getOffers(ageCheck.offerIds).then { List<Offer> offers ->
            def maxRatingAge = super.getMaxRatingAge(offers)
            if (maxRatingAge < 18 ) {
                ageCheck.setStatus(Status.PASSED)
            }
            else if (ageCheck.dob == null) {
                    ageCheck.setStatus(Status.BLOCKED)
            }
            else {
                def userAge = super.calculateAge(ageCheck.dob)
                ageCheck.setStatus( userAge >= maxRatingAge ? Status.PASSED : Status.BANNED)
            }
            return Promise.pure(ageCheck)
        }
    }

    private boolean ignoreTimes() {
        Calendar calendar = Calendar.instance
        def timeZone = TimeZone.getTimeZone(TIME_ZONE_OF_CET)
        calendar.setTimeZone(timeZone)
        int currentTime = calendar.get(Calendar.HOUR_OF_DAY)
        return currentTime < 6 || currentTime >= 23
    }
}
