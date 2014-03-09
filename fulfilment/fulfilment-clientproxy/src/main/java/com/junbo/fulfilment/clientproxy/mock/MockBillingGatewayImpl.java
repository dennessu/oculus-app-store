/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.clientproxy.mock;

import com.junbo.fulfilment.clientproxy.BillingGateway;
import com.junbo.fulfilment.spec.fusion.ShippingAddress;

/**
 * MockBillingGatewayImpl.
 */
public class MockBillingGatewayImpl implements BillingGateway {
    @Override
    public ShippingAddress getShippingAddress(Long userId, Long shippingAddressId) {
        ShippingAddress address = new ShippingAddress();
        address.setAddressId(shippingAddressId);
        address.setUserId(123L);
        address.setCity("test_city");
        address.setStreet("test_street");
        address.setStreet1("test_street1");
        address.setStreet1("test_street2");
        address.setState("test_stat");
        address.setCompanyName("test_company_name");
        address.setCountry("test_country");
        address.setFirstName("test_first_name");
        address.setMiddleName("test_middle_name");
        address.setLastName("test_last_name");
        address.setPhoneNumber("12334");
        address.setDescription("test_desc");

        return address;
    }
}
