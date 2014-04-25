/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.clientproxy;

import com.junbo.payment.spec.model.Address;

/**
 * Mock PersonalInfoClientProxy.
 */
//TODO: should replace it with the real client proxy
public class MockPersonalInfoClientProxy {
    public Address getAddress(Long addressId){
        return new Address(){
            {
                setAddressLine1("3rd street");
                setCountry("us");
                setPostalCode("12345");
            }
        };
    }
}
