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
import com.junbo.payment.spec.model.Address
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

    TaxServiceImpl() {
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
        Long piId = balance.piId.value
        return paymentFacade.getPaymentInstrument(userId, piId).recover { Throwable throwable ->
            LOGGER.error('name=Error_Get_PaymentInstrument. pi id: ' + balance.piId.value, throwable)
            throw AppErrors.INSTANCE.piNotFound(piId.toString()).exception()
        }.then { PaymentInstrument pi ->
            if (balance.shippingAddressId != null) {
                Long addressId = balance.shippingAddressId.value
                return shippingAddressService.getShippingAddress(userId, addressId)
                        .then { ShippingAddress shippingAddress ->
                    return taxFacade.calculateTax(balance, shippingAddress, pi.address)
                }
            }
            return taxFacade.calculateTax(balance, null, pi.address)
        }

    }

    @Override
    Promise<ShippingAddress> validateShippingAddress(ShippingAddress shippingAddress) {
        return taxFacade.validateShippingAddress(shippingAddress)
    }

    @Override
    Promise<Address> validatePiAddress(Address piAddress) {
        return taxFacade.validatePiAddress(piAddress)
    }
}
