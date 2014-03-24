/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.core.service;

import com.junbo.billing.spec.model.Balance;
import com.junbo.billing.spec.model.ShippingAddress;
import com.junbo.langur.core.promise.Promise;
import com.junbo.payment.spec.model.Address;

/**
 * Created by LinYi on 14-3-10.
 */
public interface TaxService {

    Promise<Balance> calculateTax(Balance balance);

    Promise<ShippingAddress> validateShippingAddress(ShippingAddress shippingAddress);

    Promise<Address> validatePiAddress(Address piAddress);
}
