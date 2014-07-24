/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.core.mock;

import com.junbo.billing.clientproxy.TaxFacade;
import com.junbo.billing.spec.enums.TaxAuthority;
import com.junbo.billing.spec.enums.TaxStatus;
import com.junbo.billing.spec.model.Balance;
import com.junbo.billing.spec.model.BalanceItem;
import com.junbo.billing.spec.model.TaxItem;
import com.junbo.billing.spec.model.VatIdValidationResponse;
import com.junbo.identity.spec.v1.model.Address;
import com.junbo.langur.core.promise.Promise;

import java.math.BigDecimal;

/**
 * Created by xmchen on 14-5-20.
 */
public class MockTaxFacade implements TaxFacade {
    @Override
    public Promise<Balance> calculateTaxQuote(Balance balance, Address shippingAddress, Address piAddress) {
        for(BalanceItem item : balance.getBalanceItems()) {
            TaxItem taxItem = new TaxItem();
            taxItem.setTaxAmount(item.getAmount().multiply(new BigDecimal("0.09")));
            taxItem.setTaxRate(new BigDecimal("0.09"));
            taxItem.setTaxAuthority(TaxAuthority.STATE.name());
            item.addTaxItem(taxItem);
        }
        balance.setTaxStatus(TaxStatus.TAXED.name());
        return Promise.pure(balance);
    }

    @Override
    public Promise<Balance> calculateTax(Balance balance, Address shippingAddress, Address piAddress) {
        return calculateTaxQuote(balance, shippingAddress, piAddress);
    }

    @Override
    public Promise<Address> validateAddress(Address address) {
        return Promise.pure(address);
    }

    @Override
    public Promise<VatIdValidationResponse> validateVatId(String vatId, String country) {
        VatIdValidationResponse response = new VatIdValidationResponse();
        response.setStatus("VALID");
        return Promise.pure(response);
    }
}
