/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.core.service

import com.junbo.billing.clientproxy.PaymentFacade
import com.junbo.billing.clientproxy.TaxFacade
import com.junbo.billing.spec.error.AppErrors
import com.junbo.billing.spec.model.Balance
import com.junbo.billing.spec.model.ShippingAddress
import com.junbo.langur.core.promise.Promise
import com.junbo.payment.spec.model.PaymentInstrument
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.annotation.Resource

/**
 * Created by LinYi on 14-3-10.
 */
@CompileStatic
class TaxServiceImpl implements TaxService {
    @Resource
    TaxFacade avalaraFacade

    @Resource
    PaymentFacade paymentFacade

    @Resource
    ShippingAddressService shippingAddressService

    String providerName

    TaxFacade taxFacade

    private static final Logger LOGGER = LoggerFactory.getLogger(TaxServiceImpl)

    void chooseProvider() {
        switch (providerName) {
            case 'AVALARA':
                taxFacade = avalaraFacade
                break
            case 'SABRIX':
                // TODO: SABRIX IMPLEMENTATION
                break
            default:
                throw AppErrors.INSTANCE.taxCalculationError('No Such Tax Provider.').exception()
        }
    }

    @Override
    Promise<Balance> calculateTax(Balance balance) {
        Long userId = balance.userId.value
        ShippingAddress shippingAddress
        if (balance.shippingAddressId != null) {
            Long addressId = balance.shippingAddressId.value
            def addressPromise = shippingAddressService.getShippingAddress(userId, addressId)
            shippingAddress = addressPromise?.wrapped().get()
        }

        Long piId = balance.piId.value
        return paymentFacade.getPaymentInstrument(userId, piId).recover { Throwable throwable ->
            LOGGER.error('name=Error_Get_PaymentInstrument. pi id: ' + balance.piId.value, throwable)
            throw AppErrors.INSTANCE.piNotFound(piId.toString()).exception()
        }.then { PaymentInstrument pi ->
            chooseProvider()
            return Promise.pure(taxFacade.calculateTax(balance, shippingAddress, pi.address))
        }
    }
}
