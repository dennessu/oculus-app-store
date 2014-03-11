/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.core.service

import com.junbo.billing.clientproxy.AvalaraFacade
import com.junbo.billing.clientproxy.PaymentFacade
import com.junbo.billing.spec.error.AppErrors
import com.junbo.billing.spec.model.Balance
import com.junbo.billing.spec.model.ShippingAddress
import com.junbo.langur.core.promise.Promise
import com.junbo.payment.spec.model.PaymentInstrument

import javax.annotation.Resource

/**
 * Created by LinYi on 14-3-10.
 */
class TaxServiceImpl implements TaxService {
    @Resource
    AvalaraFacade avalaraFacade

    @Resource
    PaymentFacade paymentFacade

    @Resource
    ShippingAddressService shippingAddressService

    @Override
    Promise<Balance> calculateTax(Balance balance) {
        Long userId = balance.userId.value
        Long addressId = balance.shippingAddressId.value
        ShippingAddress shippingAddress = shippingAddressService.getShippingAddress(userId, addressId)
        if (shippingAddress == null) {
            throw AppErrors.INSTANCE.shippingAddressNotFound(addressId.toString()).exception()
        }

        Long piId = balance.piId.value
        PaymentInstrument pi = paymentFacade.getPaymentInstrument(piId)
        if (pi == null) {
            throw AppErrors.INSTANCE.piNotFound(piId.toString())
        }
        return Promise.pure(avalaraFacade.calculateTax(balance, shippingAddress, pi.address))
    }
}
