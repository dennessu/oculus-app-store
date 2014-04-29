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
class CommonVerifierImpl extends BaseVerifier implements Verifier {
    private final static String TYPE = 'COMMON'
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
        if (ageCheck.dob != null) {
            return catalogFacade.getOffers(ageCheck.offerIds).then { List<Offer> offers ->
                def maxRatingAge = super.getMaxRatingAge(offers)
                def userAge = super.calculateAge(ageCheck.dob)
                ageCheck.setStatus(maxRatingAge > userAge ? Status.BANNED : Status.PASSED)
                return Promise.pure(ageCheck)
            }
        }
        else if (ageCheck.userId != null) {
            return identityFacade.getUserDob(ageCheck.userId.value).then { Date dob ->
                if (dob != null) {
                    def userAge = super.calculateAge(dob)
                    return catalogFacade.getOffers(ageCheck.offerIds).then { List<Offer> offers ->
                        def maxRatingAge = super.getMaxRatingAge(offers)
                        ageCheck.setStatus(maxRatingAge > userAge ? Status.BANNED : Status.PASSED)
                        return Promise.pure(ageCheck)
                    }
                }
                return catalogFacade.getOffers(ageCheck.offerIds).then { List<Offer> offers ->
                    def maxRatingAge = super.getMaxRatingAge(offers)
                    ageCheck.setStatus(maxRatingAge > 0 ? Status.BLOCKED : Status.PASSED)
                    return Promise.pure(ageCheck)
                }
            }
        }
    }
}
