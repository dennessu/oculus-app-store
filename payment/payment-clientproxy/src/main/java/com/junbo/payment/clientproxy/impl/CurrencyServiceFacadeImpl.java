/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.clientproxy.impl;

import com.junbo.common.enumid.CurrencyId;
import com.junbo.common.error.AppCommonErrors;
import com.junbo.identity.spec.v1.model.Currency;
import com.junbo.identity.spec.v1.option.model.CurrencyGetOptions;
import com.junbo.identity.spec.v1.resource.proxy.CurrencyResourceClientProxy;
import com.junbo.langur.core.promise.Promise;
import com.junbo.payment.clientproxy.CurrencyServiceFacade;
import com.junbo.payment.common.CommonUtil;
import com.junbo.payment.common.exception.AppClientExceptions;
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
        if (currencyId == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired("currency").exception();
        }
        Currency currency = currencyResource.get(currencyId, new CurrencyGetOptions()).get();
        if (currency == null) {
            throw AppClientExceptions.INSTANCE.currencyNotFound(currencyId.toString()).exception();
        }
        Long numbers = getNumberAfterDecimal(currency.getCurrencyCode()).get();
        return Promise.pure(new Long(currency.getMinAuthAmount().multiply(new BigDecimal(numbers)).longValue()));
    }

    public Promise<Long> getNumberAfterDecimal(String currencyCode){
        if (CommonUtil.isNullOrEmpty(currencyCode)) {
            throw AppCommonErrors.INSTANCE.fieldRequired("currencyCode").exception();
        }
        Currency currency = currencyResource.get(new CurrencyId(currencyCode), new CurrencyGetOptions()).get();
        if (currency == null) {
            throw AppClientExceptions.INSTANCE.currencyNotFound(currencyCode).exception();
        }
        return Promise.pure(new Long((long) Math.pow(10, currency.getNumberAfterDecimal())));
    }
}
