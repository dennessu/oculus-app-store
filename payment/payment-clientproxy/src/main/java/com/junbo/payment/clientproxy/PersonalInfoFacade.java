/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.clientproxy;

import com.junbo.langur.core.promise.Promise;
import com.junbo.payment.spec.model.Address;

/**
 * Personal Info Facade.
 */
public interface PersonalInfoFacade {
    Promise<Address> getBillingAddress(Long billingAddressId);
    Promise<String> getPhoneNumber(Long phoneNumberId);
    Promise<String> getEmail(Long emailId);
}
