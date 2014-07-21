/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.core.service

import com.junbo.billing.clientproxy.CatalogFacade
import com.junbo.billing.clientproxy.IdentityFacade
import com.junbo.billing.clientproxy.PaymentFacade
import com.junbo.billing.clientproxy.TaxFacade
import com.junbo.billing.spec.enums.PropertyKey
import com.junbo.billing.spec.enums.TaxStatus
import com.junbo.billing.spec.error.AppErrors
import com.junbo.billing.spec.model.Balance
import com.junbo.billing.spec.model.BalanceItem
import com.junbo.billing.spec.model.VatIdValidationResponse
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.OrganizationId
import com.junbo.common.id.PIType
import com.junbo.common.id.UserId
import com.junbo.common.id.UserPersonalInfoId
import com.junbo.identity.spec.v1.model.Address
import com.junbo.identity.spec.v1.model.Organization
import com.junbo.identity.spec.v1.model.UserVAT
import com.junbo.langur.core.promise.Promise
import com.junbo.payment.spec.model.PaymentInstrument
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier

/**
 * Created by LinYi on 14-3-10.
 */
@CompileStatic
class TaxServiceImpl implements TaxService {
    PaymentFacade paymentFacade

    IdentityFacade identityFacade

    CatalogFacade catalogFacade

    @Autowired
    void setIdentityFacade(@Qualifier('billingIdentityFacade')IdentityFacade identityFacade) {
        this.identityFacade = identityFacade
    }

    @Autowired
    void setPaymentFacade(@Qualifier('billingPaymentFacade')PaymentFacade paymentFacade) {
        this.paymentFacade = paymentFacade
    }

    @Autowired
    void setCatalogFacade(@Qualifier('billingCatalogFacade')CatalogFacade catalogFacade) {
        this.catalogFacade = catalogFacade
    }

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

        /* To calculate tax, we need to collect info about:
        1. payment instrument
        2. customer
        3. offer
        4. developer of offer
        * */
        Long piId = balance.piId.value
        return paymentFacade.getPaymentInstrument(piId).recover { Throwable throwable ->
            LOGGER.error('name=Error_Get_PaymentInstrument. pi id: ' + balance.piId.value, throwable)
            throw AppErrors.INSTANCE.piNotFound("balance.paymentInstrument", balance.piId).exception()
        }.then { PaymentInstrument pi ->
            if (pi.billingAddressId == null) {
                throw AppCommonErrors.INSTANCE.fieldRequired("paymentInstrument.billingAddress").exception()
            }
            balance.propertySet.put(PropertyKey.PAYMENT_METHOD.name(), PIType.get(pi.type).name())
            balance.propertySet.put(PropertyKey.BILLING_ADDRESS.name(), pi.billingAddressId.toString())
            UserId userId = balance.userId
            if (userId == null || userId.value == null) {
                throw AppCommonErrors.INSTANCE.fieldRequired("paymentInstrument.user").exception()
            }
            return identityFacade.getUser(userId.value).recover { Throwable throwable ->
                LOGGER.error('name=Error_Get_User. user id: ' + userId, throwable)
                throw AppErrors.INSTANCE.userNotFound("balance.userId", userId).exception()
            }.then { com.junbo.identity.spec.v1.model.User user ->
                balance.propertySet.put(PropertyKey.CUSTOMER_NAME.name(), user.username)
                UserVAT vat = user.vat?.get(balance.country)
                if (vat != null) {
                    balance.balanceItems.each { BalanceItem item ->
                        item.propertySet.put(PropertyKey.VAT_ID.name(), vat.vatNumber)
                    }
                }
                return calculateTax(balance, pi.billingAddressId)
            }
        }
    }

    @Override
    Promise<Address> validateAddress(Address address) {
        return taxFacade.validateAddress(address)
    }

    @Override
    Promise<VatIdValidationResponse> validateVatId(String vatId) {
        return taxFacade.validateVatId(vatId)
    }

    @Override
    Promise<Balance> auditTax(Balance balance) {
        Long shippingAddressId = balance.shippingAddressId?.value
        Long billingAddressId = Long.valueOf(balance.propertySet.get(PropertyKey.BILLING_ADDRESS.name()))
        return identityFacade.getAddress(shippingAddressId).recover { Throwable throwable ->
            LOGGER.error('name=Error_Get_Shipping_Address. address id: ' + billingAddressId, throwable)
            throw AppErrors.INSTANCE.addressNotFound("balance.shippingAddress", balance.shippingAddressId).exception()
        }.then { Address shippingAddress ->
            return identityFacade.getAddress(billingAddressId).recover { Throwable throwable ->
                LOGGER.error('name=Error_Get_Billing_Address. address id: ' + billingAddressId, throwable)
                throw AppErrors.INSTANCE.addressNotFound("billingAddress", new UserPersonalInfoId(billingAddressId)).exception()
            }.then { Address billingAddress ->
                return taxFacade.calculateTax(balance, shippingAddress, billingAddress)
            }
        }
    }

    Promise<Balance> calculateTax(Balance balance, Long billingAddressId) {
        Long addressId = balance.shippingAddressId?.value
        return identityFacade.getAddress(addressId).recover { Throwable throwable ->
            LOGGER.error('name=Error_Get_Shipping_Address. address id: ' + addressId, throwable)
            throw AppErrors.INSTANCE.addressNotFound("balance.shippingAddress", balance.shippingAddressId).exception()
        }.then { Address shippingAddress ->
            return identityFacade.getAddress(billingAddressId).recover { Throwable throwable ->
                LOGGER.error('name=Error_Get_Billing_Address. address id: ' + billingAddressId, throwable)
                throw AppErrors.INSTANCE.addressNotFound("billingAddress", new UserPersonalInfoId(billingAddressId)).exception()
            }.then { Address billingAddress ->
                return taxFacade.calculateTaxQuote(balance, shippingAddress, billingAddress)
            }
        }
    }
}
