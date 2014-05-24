/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.clientproxy.impl;

import com.junbo.common.enumid.CountryId;
import com.junbo.common.enumid.CurrencyId;
import com.junbo.identity.spec.v1.model.Country;
import com.junbo.identity.spec.v1.resource.proxy.CountryResourceClientProxy;
import com.junbo.langur.core.promise.Promise;
import com.junbo.payment.clientproxy.CountryServiceFacade;
import com.junbo.payment.common.CommonUtil;
import com.junbo.payment.common.exception.AppClientExceptions;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * country service facade.
 */
public class CountryServiceFacadeImpl implements CountryServiceFacade{
    @Autowired
    private CountryResourceClientProxy countryResource;

    public Promise<CurrencyId> getDefaultCurrency(String country){
        if(CommonUtil.isNullOrEmpty(country)){
            throw AppClientExceptions.INSTANCE.invalidCountry(country).exception();
        }
        Country count = countryResource.get(new CountryId(country), null).get();
        if(count == null){
            throw AppClientExceptions.INSTANCE.invalidCountry(country).exception();
        }
        return Promise.pure(count.getDefaultCurrency());
    }
}
