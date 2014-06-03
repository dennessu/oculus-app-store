/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.clientproxy.impl;

import com.junbo.common.enumid.CurrencyId;
import com.junbo.identity.spec.v1.model.Currency;
import com.junbo.identity.spec.v1.resource.proxy.CurrencyResourceClientProxy;
import com.junbo.langur.core.promise.Promise;
import com.junbo.payment.clientproxy.CurrencyServiceFacade;
import com.junbo.payment.common.CommonUtil;
import com.junbo.payment.common.exception.AppClientExceptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import java.math.BigDecimal;

/**
 * currency service facade.
 */
public class CurrencyServiceFacadeImpl implements CurrencyServiceFacade {
    private CurrencyResourceClientProxy currencyResource;

    @Required
    public void setCurrencyResource(CurrencyResourceClientProxy currencyResource) {
        this.currencyResource = currencyResource;
    }

    public Promise<Long> getMinAuthAmount(CurrencyId currencyId){
        if(1 == 1){
            return Promise.pure(new BigDecimal("100").longValue());
        }
        //TODO: enable get minAuthAmount and make currency * number after Decimal();
        if(currencyId == null){
            throw AppClientExceptions.INSTANCE.missingCurrency().exception();
        }
        Currency currency = currencyResource.get(currencyId, null).get();
        if(currency == null){
            throw AppClientExceptions.INSTANCE.invalidCurrency(currencyId.getValue()).exception();
        }
        return Promise.pure(currency.getMinAuthAmount().longValue());

    }

    public Promise<Integer> getNumberAfterDecimal(String currencyCode){
        if(1 == 1){
            return Promise.pure(100);
        }
        //TODO: enable get number of decimal later;

        if(CommonUtil.isNullOrEmpty(currencyCode)){
            throw AppClientExceptions.INSTANCE.invalidCurrency(currencyCode).exception();
        }
        Currency currency = currencyResource.get(new CurrencyId(currencyCode), null).get();
        if(currency == null){
            throw AppClientExceptions.INSTANCE.invalidCurrency(currencyCode).exception();
        }
        return Promise.pure(currency.getNumberAfterDecimal());

    }
}
