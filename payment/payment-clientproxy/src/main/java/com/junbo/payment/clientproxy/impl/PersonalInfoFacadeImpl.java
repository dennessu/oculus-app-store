/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.clientproxy.impl;

import com.junbo.langur.core.promise.Promise;
import com.junbo.payment.clientproxy.MockPersonalInfoClientProxy;
import com.junbo.payment.clientproxy.PersonalInfoFacade;
import com.junbo.payment.spec.model.Address;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * personal info client proxy facade implementation.
 */
public class PersonalInfoFacadeImpl implements PersonalInfoFacade {
    @Autowired
    private MockPersonalInfoClientProxy mockPersonalInfoClientProxy;
    @Override
    public Promise<Address> getBillingAddress(Long billingAddressId) {
        if(billingAddressId == null){
            return Promise.pure(null);
        }
        return Promise.pure(mockPersonalInfoClientProxy.getAddress(billingAddressId));
    }

    @Override
    public Promise<String> getPhoneNumber(Long phoneNumberId) {
        return null;
    }

    @Override
    public Promise<String> getEmail(Long emailId) {
        return null;
    }
}
