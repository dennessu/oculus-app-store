/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.core.service

import com.junbo.billing.clientproxy.PaymentFacade
import com.junbo.billing.clientproxy.TaxFacade
import com.junbo.billing.spec.enums.TaxStatus
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

    TaxFacade taxFacade

    String providerName

    private static final Logger LOGGER = LoggerFactory.getLogger(TaxServiceImpl)

    void chooseTaxProvider() {
        if (taxFacade != null) {
            return
        }
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
        if (balance.skipTaxCalculation) {
            balance.taxStatus = TaxStatus.NOT_TAXED.name()
            return Promise.pure(balance)
        }

        chooseTaxProvider()

        Long userId = balance.userId.value
        Long piId = balance.piId.value
        return paymentFacade.getPaymentInstrument(piId).recover { Throwable throwable ->
            LOGGER.error('name=Error_Get_PaymentInstrument. pi id: ' + balance.piId.value, throwable)
            throw AppErrors.INSTANCE.piNotFound(piId.toString()).exception()
        }.then { PaymentInstrument pi ->
            if (balance.shippingAddressId != null) {
                Long addressId = balance.shippingAddressId.value
                return shippingAddressService.getShippingAddress(userId, addressId)
                        .then { ShippingAddress shippingAddress ->
                    return taxFacade.validateShippingAddress(shippingAddress)
                            .then { ShippingAddress validatedShippingAddress ->
                        return taxFacade.calculateTax(balance, validatedShippingAddress, pi.address)
                    }
                }
            }
            return taxFacade.validatePiAddress(pi.address).then { Address validatedPiAddress ->
                return taxFacade.calculateTax(balance, null, validatedPiAddress)
            }
        }

    }

    @Override
    Promise<ShippingAddress> validateShippingAddress(ShippingAddress shippingAddress) {
        chooseTaxProvider()

        return taxFacade.validateShippingAddress(shippingAddress)
    }

    @Override
    Promise<Address> validatePiAddress(Address piAddress) {
        chooseTaxProvider()

        return taxFacade.validatePiAddress(piAddress)
    }
}
