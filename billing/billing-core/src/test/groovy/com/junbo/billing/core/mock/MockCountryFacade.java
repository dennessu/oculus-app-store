/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.core.mock;

import com.junbo.billing.clientproxy.CountryFacade;
import com.junbo.common.enumid.CountryId;
import com.junbo.identity.spec.v1.model.Country;
import com.junbo.langur.core.promise.Promise;

/**
 * Created by xmchen on 14-6-4.
 */
public class MockCountryFacade implements CountryFacade {
    @Override
    public Promise<Country> getCountry(String countryId) {
        Country country = new Country();
        country.setCountryCode("US");
        country.setId(new CountryId("US"));
        return Promise.pure(country);
    }
}
