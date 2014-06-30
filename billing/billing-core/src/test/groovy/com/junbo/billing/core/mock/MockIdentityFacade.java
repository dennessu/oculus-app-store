/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.core.mock;

import com.junbo.billing.clientproxy.IdentityFacade;
import com.junbo.common.enumid.CountryId;
import com.junbo.common.id.OrganizationId;
import com.junbo.common.id.UserId;
import com.junbo.identity.spec.v1.model.Address;
import com.junbo.identity.spec.v1.model.Organization;
import com.junbo.identity.spec.v1.model.User;
import com.junbo.langur.core.promise.Promise;

/**
 * Created by xmchen on 14-5-19.
 */
public class MockIdentityFacade implements IdentityFacade {
    @Override
    public Promise<User> getUser(Long userId) {
        User user = new User();
        user.setId(new UserId(userId));
        user.setStatus("ACTIVE");

        return Promise.pure(user);
    }

    @Override
    public Promise<Address> getAddress(Long addressId) {
        Address address = new Address();

        address.setStreet1("No. 1000, Twin Dolphin Dr");
        address.setCity("Redwood City");
        address.setSubCountry("CA");
        address.setCountryId(new CountryId("US"));
        address.setPostalCode("94065");

        return Promise.pure(address);
    }

    @Override
    public Promise<Organization> getOrganization(Long organizationId) {
        Organization organization = new Organization();

        organization.setName("test organization");
        organization.setId(new OrganizationId(123));

        return Promise.pure(organization);
    }
}
