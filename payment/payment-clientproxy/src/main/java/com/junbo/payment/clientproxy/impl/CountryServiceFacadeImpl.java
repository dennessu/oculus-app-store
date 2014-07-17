/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.clientproxy.impl;

import com.junbo.common.enumid.CountryId;
import com.junbo.common.enumid.CurrencyId;
import com.junbo.identity.spec.v1.model.Country;
import com.junbo.identity.spec.v1.option.model.CountryGetOptions;
import com.junbo.identity.spec.v1.resource.proxy.CountryResourceClientProxy;
import com.junbo.langur.core.promise.Promise;
import com.junbo.payment.clientproxy.CountryServiceFacade;
import com.junbo.payment.common.CommonUtil;
import com.junbo.payment.common.exception.AppClientExceptions;
import org.springframework.beans.factory.annotation.Required;


/**
 * country service facade.
 */
public class CountryServiceFacadeImpl implements CountryServiceFacade{
    private CountryResourceClientProxy countryResource;

    @Required
    public void setCountryResource(CountryResourceClientProxy countryResource) {
        this.countryResource = countryResource;
    }

    public Promise<CurrencyId> getDefaultCurrency(String country){
        if (CommonUtil.isNullOrEmpty(country)) {
            throw AppClientExceptions.INSTANCE.invalidCountry(country).exception();
        }
        Country count = countryResource.get(new CountryId(country), new CountryGetOptions()).get();
        if (count == null) {
            throw AppClientExceptions.INSTANCE.invalidCountry(country).exception();
        }
        return Promise.pure(count.getDefaultCurrency());
    }
}
