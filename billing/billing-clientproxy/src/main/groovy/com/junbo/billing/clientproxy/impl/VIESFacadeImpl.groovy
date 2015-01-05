/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.clientproxy.impl

import com.junbo.billing.clientproxy.TaxFacade
import com.junbo.billing.clientproxy.impl.wsdl.vies.CheckVat
import com.junbo.billing.clientproxy.impl.wsdl.vies.CheckVatResponse
import com.junbo.billing.spec.model.Balance
import com.junbo.billing.spec.model.VatIdValidationResponse
import com.junbo.identity.spec.v1.model.Address
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.ws.client.core.support.WebServiceGatewaySupport
import org.springframework.ws.soap.client.SoapFaultClientException

/**
 * Implementation of VIES facade to validate VAT number.
 */
@CompileStatic
class VIESFacadeImpl extends WebServiceGatewaySupport implements TaxFacade {
    private static final Logger LOGGER = LoggerFactory.getLogger(VIESFacadeImpl)

    @Override
    Promise<Balance> calculateTaxQuote(Balance balance, Address shippingAddress, Address piAddress) {
        LOGGER.error('Should_not_Use_VIES_To_Calculate_Tax')
        return Promise.pure(null)
    }

    @Override
    Promise<Balance> calculateTax(Balance balance, Address shippingAddress, Address piAddress) {
        LOGGER.error('Should_not_Use_VIES_To_Calculate_Tax')
        return Promise.pure(null)
    }

    @Override
    Promise<Address> validateAddress(Address address) {
        LOGGER.error('Should_not_Use_VIES_To_Validate_Address')
        return Promise.pure(null)
    }

    @Override
    Promise<VatIdValidationResponse> validateVatId(String vatId, String country) {
        CheckVat request = new CheckVat()
        request.countryCode = country
        request.vatNumber = vatId
        try {
            CheckVatResponse response = (CheckVatResponse) getWebServiceTemplate().marshalSendAndReceive(request)
            return Promise.pure(buildResult(response))
        }
        catch (SoapFaultClientException ex) {
            LOGGER.warn('name=VAT_Validation_Failed_for_SOAP_Fault', ex)
            return Promise.pure(buildResult(request, ex))
        }
        catch (Exception ex) {
            LOGGER.warn('name=VAT_Validation_Failed', ex)
            return Promise.pure(buildResult(request, ex))
        }
    }

    private VatIdValidationResponse buildResult(CheckVatResponse response) {
        VatIdValidationResponse result = new VatIdValidationResponse()
        result.vatId = response.vatNumber
        if (response.valid) {
            result.status = 'VALID'
            result.address = response.address?.value
            result.companyName = response.name?.value
            result.message = 'Yes, valid VAT number.'
        }
        else {
            result.status = 'INVALID'
            result.message = 'No, invalid VAT number.'
        }

        return result
    }

    private VatIdValidationResponse buildResult(CheckVat request, Exception ex) {
        VatIdValidationResponse result = new VatIdValidationResponse()
        if (ex instanceof SoapFaultClientException) {
            result.vatId = request.vatNumber
            String faultString = ex.faultStringOrReason
            if (faultString.contains('INVALID_INPUT')) {
                result.status = 'INVALID'
                result.message = 'Error: Incomplete (VAT + Member State) or corrupted data input.'
            } else if (faultString.contains('SERVICE_UNAVAILABLE') || faultString.contains('SERVER_BUSY')) {
                result.status = 'SERVICE_UNAVAILABLE'
                result.message = 'Service unavailable. Please re-submit your request later.'
            } else if (faultString.contains('MS_UNAVAILABLE')) {
                result.status = 'SERVICE_UNAVAILABLE'
                result.message = 'Member state service unavailable. Please re-submit your request later.'
            } else if (faultString.contains('TIMEOUT')) {
                result.status = 'SERVICE_UNAVAILABLE'
                result.message = 'Request time-out. Please re-submit your request later.'
            } else {
                result.status = 'UNKNOWN'
                result.message = faultString
            }
        } else {
            result.status = 'UNKNOWN'
            result.message = 'Fail to validate VAT number'
        }

        return result
    }
}
