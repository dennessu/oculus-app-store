/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.core.service

import com.junbo.billing.clientproxy.IdentityFacade
import com.junbo.billing.clientproxy.PaymentFacade
import com.junbo.billing.clientproxy.TaxFacade
import com.junbo.billing.spec.enums.TaxStatus
import com.junbo.billing.spec.error.AppErrors
import com.junbo.billing.spec.model.Balance
import com.junbo.billing.spec.model.BalanceItem
import com.junbo.identity.spec.v1.model.Address
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
    @Resource(name='billingPaymentFacade')
    PaymentFacade paymentFacade

    @Resource(name='billingIdentityFacade')
    IdentityFacade identityFacade

    TaxFacade taxFacade

    Map<String, TaxFacade> map

    String providerName

    private static final Logger LOGGER = LoggerFactory.getLogger(TaxServiceImpl)

    TaxServiceImpl(Map<String, TaxFacade> map, String providerName) {
        if (map == null || providerName == null) {
            throw AppErrors.INSTANCE.taxCalculationError('Fail to load tax configuration.').exception()
        }
        this.map = map
        this.providerName = providerName
        this.taxFacade = map.get(providerName)
        if (taxFacade == null) {
            throw AppErrors.INSTANCE.taxCalculationError('Fail to load tax configuration.').exception()
        }
    }

    @Override
    Promise<Balance> calculateTax(Balance balance) {
        if (balance.balanceItems?.any { BalanceItem item ->
            item.taxItems != null && item.taxItems?.size() > 0
        }) {
            // tax already calculated
            balance.taxStatus = TaxStatus.TAXED.name()
            return Promise.pure(balance)
        }
        if (balance.skipTaxCalculation) {
            balance.taxStatus = TaxStatus.NOT_TAXED.name()
            return Promise.pure(balance)
        }

        Long piId = balance.piId.value
        return paymentFacade.getPaymentInstrument(piId).recover { Throwable throwable ->
            LOGGER.error('name=Error_Get_PaymentInstrument. pi id: ' + balance.piId.value, throwable)
            throw AppErrors.INSTANCE.piNotFound(piId.toString()).exception()
        }.then { PaymentInstrument pi ->
            if (pi.billingAddressId == null) {
                throw AppErrors.INSTANCE.addressNotFound("null").exception()
            }
            if (balance.shippingAddressId != null) {
                Long addressId = balance.shippingAddressId.value
                return identityFacade.getAddress(addressId).then { Address shippingAddress ->
                    return taxFacade.validateAddress(shippingAddress)
                }.then { Address validatedShippingAddress ->
                    return identityFacade.getAddress(pi.billingAddressId).then { Address address ->
                        return taxFacade.validateAddress(address)
                    }.then { Address validatedPiAddress ->
                        return taxFacade.calculateTax(balance, validatedShippingAddress, validatedPiAddress)
                    }
                }
            }
            return identityFacade.getAddress(pi.billingAddressId).then { Address address ->
                return taxFacade.validateAddress(address).then { Address validatedPiAddress ->
                    return taxFacade.calculateTax(balance, null, validatedPiAddress)
                }
            }
        }

    }

    @Override
    Promise<Address> validateAddress(Address address) {
        return taxFacade.validateAddress(address)
    }
}
