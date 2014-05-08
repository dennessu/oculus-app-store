/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.clientproxy.impl

import com.junbo.billing.clientproxy.TaxFacade
import com.junbo.billing.spec.model.Balance
import com.junbo.billing.spec.model.ShippingAddress
import com.junbo.identity.spec.v1.model.Address
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Implementation of Sabrix facade to calculate tax & validate address.
 */
@CompileStatic
class SabrixFacadeImpl implements TaxFacade {
    @Override
    Promise<Balance> calculateTax(Balance balance, ShippingAddress shippingAddress, Address piAddress) {
        return null
    }

    @Override
    Promise<ShippingAddress> validateShippingAddress(ShippingAddress shippingAddress) {
        return null
    }

    @Override
    Promise<Address> validateAddress(Address address) {
        return null
    }
}
