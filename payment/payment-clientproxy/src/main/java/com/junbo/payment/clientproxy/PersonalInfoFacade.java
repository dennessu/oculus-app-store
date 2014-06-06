/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.clientproxy;

import com.junbo.common.id.AddressId;
import com.junbo.identity.spec.v1.model.Address;
import com.junbo.langur.core.promise.Promise;

/**
 * Personal Info Facade.
 */
public interface PersonalInfoFacade {
    Promise<AddressId> createBillingAddress(final Long userId, Address address);
    Promise<com.junbo.payment.spec.model.Address> getBillingAddress(Long billingAddressId);
    Promise<String> getPhoneNumber(Long phoneNumberId);
    Promise<String> getEmail(Long emailId);
}
