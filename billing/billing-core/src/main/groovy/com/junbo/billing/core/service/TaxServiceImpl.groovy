/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.core.service

import com.junbo.billing.clientproxy.IdentityFacade
import com.junbo.billing.clientproxy.PaymentFacade
import com.junbo.billing.clientproxy.TaxFacade
import com.junbo.billing.spec.enums.PropertyKey
import com.junbo.billing.spec.enums.TaxStatus
import com.junbo.billing.spec.error.AppErrors
import com.junbo.billing.spec.model.Balance
import com.junbo.billing.spec.model.BalanceItem
import com.junbo.billing.spec.model.VatIdValidationResponse
import com.junbo.common.enumid.CountryId
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.PIType
import com.junbo.common.id.UserId
import com.junbo.common.id.UserPersonalInfoId
import com.junbo.identity.spec.v1.model.Address
import com.junbo.identity.spec.v1.model.TaxExempt
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.model.UserVAT
import com.junbo.langur.core.promise.Promise
import com.junbo.payment.spec.model.PaymentInstrument
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value

/**
 * Created by LinYi on 14-3-10.
 */
@CompileStatic
class TaxServiceImpl implements TaxService {
    PaymentFacade paymentFacade

    IdentityFacade identityFacade

    @Autowired
    void setIdentityFacade(@Qualifier('billingIdentityFacade') IdentityFacade identityFacade) {
        this.identityFacade = identityFacade
    }

    @Autowired
    void setPaymentFacade(@Qualifier('billingPaymentFacade') PaymentFacade paymentFacade) {
        this.paymentFacade = paymentFacade
    }

    @Value('${gsa.purchase.prefix}')
    String gsaPrefix

    TaxFacade taxFacade

    TaxFacade vatFacade

    Map<String, TaxFacade> map

    String providerName

    private static final Logger LOGGER = LoggerFactory.getLogger(TaxServiceImpl)

    TaxServiceImpl(Map<String, TaxFacade> map, String providerName, String vatProviderName) {
        if (map == null || providerName == null) {
            throw AppErrors.INSTANCE.taxCalculationError('Fail to load tax configuration.').exception()
        }
        this.map = map
        this.providerName = providerName
        this.taxFacade = map.get(providerName)
        this.vatFacade = map.get(vatProviderName)
        if (taxFacade == null || vatFacade == null) {
            throw AppErrors.INSTANCE.taxCalculationError('Fail to load tax configuration.').exception()
        }
    }

    @Override
    Promise<Balance> calculateTax(Balance balance) {
        if (balance.skipTaxCalculation) {
            if (balance.taxStatus == null) {
                balance.taxStatus = TaxStatus.TAXED.name()
            }
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
            /* use ipGeoLocation as address if billing address not found*/
            if (pi.billingAddressId == null && balance.propertySet.get(PropertyKey.IP_GEO_LOCATION.name()) == null) {
                throw AppCommonErrors.INSTANCE.fieldRequired("paymentInstrument.billingAddress").exception()
            }
            balance.propertySet.put(PropertyKey.PAYMENT_METHOD.name(), PIType.get(pi.type).name())
            if (pi.billingAddressId != null) {
                balance.propertySet.put(PropertyKey.BILLING_ADDRESS.name(), pi.billingAddressId.toString())
            }
            if (PIType.get(pi.type) == PIType.CREDITCARD) {
                balance.propertySet.put(PropertyKey.BIN_NUMBER.name(), pi.typeSpecificDetails?.issuerIdentificationNumber)
            }
            UserId userId = balance.userId
            if (userId == null || userId.value == null) {
                throw AppCommonErrors.INSTANCE.fieldRequired("paymentInstrument.user").exception()
            }
            return identityFacade.getUser(userId.value).recover { Throwable throwable ->
                LOGGER.error('name=Error_Get_User. user id: ' + userId, throwable)
                throw AppErrors.INSTANCE.userNotFound("balance.userId", userId).exception()
            }.then { User user ->
                return identityFacade.getUsername(user.username.value).then { String username ->
                    balance.propertySet.put(PropertyKey.CUSTOMER_NAME.name(), username)
                    return calculateTax(balance, pi.billingAddressId, user)
                }
            }
        }
    }

    @Override
    Promise<Address> validateAddress(Address address) {
        return taxFacade.validateAddress(address)
    }

    @Override
    Promise<VatIdValidationResponse> validateVatId(String vatId, String country) {
        return vatFacade.validateVatId(vatId, country)
    }

    @Override
    Promise<Balance> auditTax(Balance balance) {
        Long shippingAddressId = balance.shippingAddressId?.value
        def billingAddressId
        if (balance.propertySet.get(PropertyKey.BILLING_ADDRESS.name()) != null) {
            billingAddressId = Long.valueOf(balance.propertySet.get(PropertyKey.BILLING_ADDRESS.name()))
        }
        return identityFacade.getAddress(shippingAddressId).recover { Throwable throwable ->
            LOGGER.error('name=Error_Get_Shipping_Address. address id: ' + billingAddressId, throwable)
            throw AppErrors.INSTANCE.addressNotFound("balance.shippingAddress", balance.shippingAddressId).exception()
        }.then { Address shippingAddress ->
            return identityFacade.getAddress(billingAddressId).recover { Throwable throwable ->
                LOGGER.error('name=Error_Get_Billing_Address. address id: ' + billingAddressId, throwable)
                throw AppErrors.INSTANCE.addressNotFound("billingAddress", new UserPersonalInfoId(billingAddressId)).exception()
            }.then { Address billingAddress ->
                if (billingAddress == null) {
                    billingAddress = buildAddressByIpGeoLocation(balance.propertySet.get(PropertyKey.IP_GEO_LOCATION.name()))
                }
                return taxFacade.calculateTax(balance, shippingAddress, billingAddress)
            }
        }
    }

    Promise<Balance> calculateTax(Balance balance, Long billingAddressId, User user) {
        Long addressId = balance.shippingAddressId?.value
        return identityFacade.getAddress(addressId).recover { Throwable throwable ->
            LOGGER.error('name=Error_Get_Shipping_Address. address id: ' + addressId, throwable)
            throw AppErrors.INSTANCE.addressNotFound("balance.shippingAddress", balance.shippingAddressId).exception()
        }.then { Address shippingAddress ->
            return identityFacade.getAddress(billingAddressId).recover { Throwable throwable ->
                LOGGER.error('name=Error_Get_Billing_Address. address id: ' + billingAddressId, throwable)
                throw AppErrors.INSTANCE.addressNotFound("billingAddress", new UserPersonalInfoId(billingAddressId)).exception()
            }.then { Address billingAddress ->
                if (billingAddress == null) {
                    billingAddress = buildAddressByIpGeoLocation(balance.propertySet.get(PropertyKey.IP_GEO_LOCATION.name()))
                }
                UserVAT vat = user.vat?.get(billingAddress.countryId.value)
                if (vat != null) {
                    def vatNumber = vat.vatNumber
                    if (vatNumber != null && vatNumber != '') {
                        balance.balanceItems.each { BalanceItem item ->
                            item.propertySet.put(PropertyKey.VAT_ID.name(), vatNumber)
                        }
                    }
                }

                TaxExempt taxExempt = getTaxExempt(user, billingAddress)
                if (taxExempt != null) {
                    def exemptReason = taxExempt.taxExemptionReason
                    def exemptCertificate = taxExempt.taxExemptionCertificateNumber
                    if (exemptReason != null && exemptReason != '') {
                        balance.propertySet.put(PropertyKey.EXEMPT_REASON.name(), taxExempt.taxExemptionReason)
                    }
                    if (exemptCertificate != null && exemptCertificate != '') {
                        balance.propertySet.put(PropertyKey.EXEMPT_CERTIFICATE.name(), taxExempt.taxExemptionCertificateNumber)
                    }
                }

                if (balance.propertySet.get(PropertyKey.BIN_NUMBER.name()) != null) {
                    def binNumber = balance.propertySet.get(PropertyKey.BIN_NUMBER.name())
                    if (isGSACard(binNumber)) {
                        balance.propertySet.put(PropertyKey.EXEMPT_REASON.name(), 'Government')
                    }
                }

                return taxFacade.calculateTaxQuote(balance, shippingAddress, billingAddress)
            }
        }
    }

    TaxExempt getTaxExempt(User user, Address billingAddress) {
        if (user.taxExemption == null || user.taxExemption.size() == 0) {
            return null
        }
        def taxExempt = user.taxExemption.find { TaxExempt exempt ->
            isValidExemption(billingAddress, exempt)
        }

        return taxExempt
    }

    Boolean isValidExemption(Address billingAddress, TaxExempt exempt) {
        if (!exempt.isTaxExemptionValidated) {
            return false
        }
        if (billingAddress.countryId.value != exempt.taxExemptionCountry) {
            return false
        }
        if (exempt.taxExemptionSubcountry == null) {
            return false
        } else if (exempt.taxExemptionSubcountry.toUpperCase() != 'ALL' &&
                exempt.taxExemptionSubcountry != billingAddress.subCountry) {
            return false
        }
        def now = new Date()
        if (exempt.taxExemptionStartDate != null && exempt.taxExemptionStartDate.after(now)) {
            return false
        }
        if (exempt.taxExemptionEndDate != null && exempt.taxExemptionEndDate.before(now)) {
            return false
        }

        return true
    }

    Boolean isGSACard(String number) {
        def prefix = number?.substring(0, 4)
        return (gsaPrefix.contains(prefix))
    }

    Address buildAddressByIpGeoLocation(String ipGeoCountry) {
        def address = new Address()
        address.countryId = new CountryId(ipGeoCountry)

        return address
    }
}
