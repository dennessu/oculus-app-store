/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.clientproxy;

import com.junbo.billing.spec.model.Balance;
import com.junbo.billing.spec.model.ShippingAddress;
import com.junbo.identity.spec.v1.model.Address;
import com.junbo.langur.core.promise.Promise;

/**
 * Created by LinYi on 14-3-13.
 */
public interface TaxFacade {
    Promise<Balance> calculateTax(Balance balance, ShippingAddress shippingAddress, Address piAddress);

    Promise<ShippingAddress> validateShippingAddress(ShippingAddress shippingAddress);

    Promise<Address> validateAddress(Address address);
}
