/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.clientproxy.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.junbo.billing.clientproxy.TaxFacade
import com.junbo.billing.clientproxy.impl.avalara.*
import com.junbo.billing.spec.enums.TaxAuthority
import com.junbo.billing.spec.enums.TaxStatus
import com.junbo.billing.spec.error.AppErrors
import com.junbo.billing.spec.model.Balance
import com.junbo.billing.spec.model.BalanceItem
import com.junbo.billing.spec.model.TaxItem
import com.junbo.billing.spec.model.VatIdValidationResponse
import com.junbo.common.enumid.CountryId
import com.junbo.identity.spec.v1.model.Address
import com.junbo.langur.core.async.JunboAsyncHttpClient
import com.junbo.langur.core.async.JunboAsyncHttpClient.BoundRequestBuilder
import com.junbo.langur.core.client.MessageTranscoder
import com.junbo.langur.core.promise.Promise
import com.ning.http.client.Response
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.annotation.Resource

import static com.ning.http.client.extra.ListenableFutureAdapter.asGuavaFuture

/**
 * Created by LinYi on 14-3-10.
 */
@CompileStatic
class AvalaraFacadeImpl implements TaxFacade {
    @Resource(name = 'avalaraConfiguration')
    AvalaraConfiguration configuration

    @Resource(name = 'commonAsyncHttpClient')
    JunboAsyncHttpClient asyncHttpClient

    @Resource(name = 'transcoder')
    MessageTranscoder transcoder

    static final String[] SUPPORT_COUNTRY_LIST = ['US', 'CA']

    private static final Logger LOGGER = LoggerFactory.getLogger(AvalaraFacadeImpl)


    @Override
    Promise<Balance> calculateTaxQuote(Balance balance, Address shippingAddress, Address piAddress) {
        GetTaxRequest request = generateGetTaxRequest(balance, shippingAddress, piAddress)
        LOGGER.info('name=Get_Tax_Request, request={}', request.toString())
        return calculateTax(request).then { GetTaxResponse response ->
            return Promise.pure(updateBalance(response, balance))
        }
    }

    @Override
    Promise<Balance> calculateTax(Balance balance, Address shippingAddress, Address piAddress) {
        return calculateTaxQuote(balance, shippingAddress, piAddress)
    }

    @Override
    Promise<Address> validateAddress(Address address) {
        if (SUPPORT_COUNTRY_LIST.contains(address.countryId.value.trim().toUpperCase())) {
            AvalaraAddress externalAddress = getAvalaraAddress(address)
            LOGGER.info('name=Validate_Address_Request, request={}', externalAddress.toString())
            return validateAvalaraAddress(externalAddress).then { ValidateAddressResponse response ->
                return Promise.pure(updateAddress(response, address))
            }
        }

        return Promise.pure(address)
    }

    @Override
    Promise<VatIdValidationResponse> validateVatId(String vatId) {
        def response = new VatIdValidationResponse()
        response.status = 'FAILED'
        response.message = 'Avalara DO NOT support VAT ID validation.'
        return Promise.pure(response)
    }

    Address updateAddress(ValidateAddressResponse response, Address address) {
        if (response != null && response.resultCode == SeverityLevel.Success) {
            address.street1 = response.address.line1
            address.street2 = response.address.line2
            address.street3 = response.address.line3
            address.city = response.address.city
            address.subCountry = response.address.region
            address.postalCode = response.address.postalCode
            address.countryId = new CountryId(response.address.country)
        } else {
            LOGGER.error('name=Address_Validation_Response_Invalid.')
            throw AppErrors.INSTANCE.addressValidationError('Invalid response.').exception()
        }
        return address
    }

    Balance updateBalance(GetTaxResponse response, Balance balance) {
        if (response != null && response.resultCode == SeverityLevel.Success) {
            balance.taxAmount = response.totalTax
            balance.balanceItems.eachWithIndex { BalanceItem item, int index ->
                response.taxLines.each { TaxLine line ->
                    if (index == Long.valueOf(line.lineNo)) {
                        item.taxAmount = BigDecimal.valueOf(line.tax)
                        line.taxDetails.each { TaxDetail detail ->
                            def taxItem = new TaxItem()
                            taxItem.taxAmount = BigDecimal.valueOf(detail.tax)
                            taxItem.taxRate = BigDecimal.valueOf(detail.rate)
                            taxItem.taxAuthority = getTaxAuthority(detail.jurisType)
                            item.addTaxItem(taxItem)
                        }
                    }
                }
            }
            balance.setTaxStatus(TaxStatus.TAXED.name())
        } else {
            LOGGER.info('name=Tax_Calculation_Failure.')
            balance.setTaxStatus(TaxStatus.FAILED.name())
        }
        return balance
    }

    String getTaxAuthority(String jurisType) {
        if (jurisType == null) {
            return null
        }

        if (EnumSet.allOf(TaxAuthority).toString().contains(jurisType.toUpperCase())) {
            return jurisType.toUpperCase()
        }
        return TaxAuthority.UNKNOWN.toString()
    }

    BoundRequestBuilder buildRequest(String url, String content) {
        def requestBuilder = asyncHttpClient.preparePost(url)
        requestBuilder.addHeader('Content-Type', 'application/x-www-form-urlencoded')
        requestBuilder.addHeader('Authorization', configuration.authorization)
        requestBuilder.addHeader('Content-Length', content.size().toString())
        requestBuilder.setBody(content)
        requestBuilder.setUrl(url)
        return requestBuilder
    }

    BoundRequestBuilder buildRequest(String url, AvalaraAddress address) {
        def requestBuilder = asyncHttpClient.prepareGet(url)
        if (address.line1 != null) {
            requestBuilder.addQueryParameter('Line1', address.line1)
        }
        if (address.line2 != null) {
            requestBuilder.addQueryParameter('Line2', address.line2)
        }
        if (address.line3 != null) {
            requestBuilder.addQueryParameter('Line3', address.line3)
        }
        if (address.city != null) {
            requestBuilder.addQueryParameter('City', address.city)
        }
        if (address.region != null) {
            requestBuilder.addQueryParameter('Region', address.region)
        }
        if (address.postalCode != null) {
            requestBuilder.addQueryParameter('PostalCode', address.postalCode)
        }
        if (address.country != null) {
            requestBuilder.addQueryParameter('Country', address.country)
        }
        requestBuilder.addHeader('Content-Type', 'application/x-www-form-urlencoded')
        requestBuilder.addHeader('Authorization', configuration.authorization)
        requestBuilder.setUrl(url)
        return requestBuilder
    }

    Promise<ValidateAddressResponse> validateAvalaraAddress(AvalaraAddress address) {
        String validateAddressUrl = configuration.baseUrl + 'address/validate'
        def requestBuilder = buildRequest(validateAddressUrl, address)
        return Promise.wrap(asGuavaFuture(requestBuilder.execute())).recover { Throwable throwable ->
            LOGGER.error('Error_Build_Avalara_Request.', throwable)
            throw AppErrors.INSTANCE.addressValidationError('Fail to build request.').exception()
        }.then { Response response ->
            ValidateAddressResponse validateAddressResponse
            try {
                validateAddressResponse = new ObjectMapper().readValue(response.responseBody,
                        ValidateAddressResponse)
            } catch (IOException ex) {
                LOGGER.error('name=Error_Read_Avalara_Response.', ex)
                throw AppErrors.INSTANCE.addressValidationError('Fail to read response.').exception()
            }
            if (response.statusCode / 100 == 2) {
                return Promise.pure(validateAddressResponse)
            }

            LOGGER.error('name=Error_Address_Validation.')
            LOGGER.info('name=Address_Validation_Response_Status_Code, statusCode={}', response.statusCode)
            String detail = ''
            validateAddressResponse.messages.each { ResponseMessage message ->
                LOGGER.info('name=Address_Validation_Response_Error_Message, message={}', message.details)
                if (message.refersTo != null) {
                    detail += 'Field: ' + message.refersTo + '. Detail: '
                }
                detail += message.details
            }
            throw AppErrors.INSTANCE.addressValidationError(detail).exception()
        }
    }

    Promise<GetTaxResponse> calculateTax(GetTaxRequest request) {
        String getTaxUrl = configuration.baseUrl + 'tax/get'
        String content = transcoder.encode(request)
        def requestBuilder = buildRequest(getTaxUrl, content)
        return Promise.wrap(asGuavaFuture(requestBuilder.execute())).recover { Throwable throwable ->
            LOGGER.error('Error_Build_Avalara_Request.', throwable)
            return Promise.pure(null)
        }.then { Response response ->
            if (response.statusCode / 100 == 2) {
                try {
                    return Promise.pure(new ObjectMapper().readValue(response.responseBody, GetTaxResponse))
                } catch (IOException ex) {
                    LOGGER.error('name=Error_Read_Avalara_Response.', ex)
                    return Promise.pure(null)
                }
            }
            else {
                LOGGER.error('name=Error_Tax_Calculation. statusCode: ' + response.statusCode)
                return Promise.pure(null)
            }
        }

    }

    GetTaxRequest generateGetTaxRequest(Balance balance, Address shippingAddress, Address piAddress) {
        GetTaxRequest request = new GetTaxRequest()
        request.companyCode = configuration.companyCode
        Date date = new Date()
        request.docDate = new java.sql.Date(date.time)
        request.customerCode = configuration.customerCode
        request.detailLevel = DetailLevel.valueOf(configuration.detailLevel)
        // TODO: append optional parameters

        // Addresses
        def addresses = []
        def shipToAddress = new AvalaraAddress()
        if (shippingAddress != null) {
            shipToAddress = getAvalaraAddress(shippingAddress)
        }
        else {
            shipToAddress = getAvalaraAddress(piAddress)
        }
        addresses << shipToAddress

        def shipFromAddress = avalaraShipFromAddress
        addresses << shipFromAddress
        request.addresses = addresses

        // lines
        def lines = []
        balance.balanceItems.eachWithIndex { BalanceItem item, int index ->
            def line = new Line()
            line.lineNo = index.toString()
            line.destinationCode = shipToAddress.addressCode
            line.originCode = shipFromAddress.addressCode
            line.qty = BigDecimal.ONE
            line.amount = item.amount
            line.itemCode = item.financeId
            lines << line
        }

        request.lines = lines
        return request
    }

    AvalaraAddress getAvalaraAddress(Address address) {
        def avalaraAddress = new AvalaraAddress()
        avalaraAddress.addressCode = '0'
        avalaraAddress.line1 = address.street1
        avalaraAddress.line2 = address.street2
        avalaraAddress.line3 = address.street3
        avalaraAddress.city = address.city
        avalaraAddress.region = address.subCountry
        avalaraAddress.postalCode = address.postalCode
        avalaraAddress.country = address.countryId.value

        return avalaraAddress
    }

    AvalaraAddress getAvalaraShipFromAddress() {
        def address = new AvalaraAddress()
        address.addressCode = '1'
        address.line1 = configuration.shipFromStreet
        address.city = configuration.shipFromCity
        address.region = configuration.shipFromState
        address.postalCode = configuration.shipFromPostalCode
        address.country = configuration.shipFromCountry

        return address
    }
}
