/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.clientproxy.impl

import static com.ning.http.client.extra.ListenableFutureAdapter.asGuavaFuture

import com.junbo.billing.clientproxy.impl.avalara.ResponseMessage
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import com.junbo.billing.clientproxy.impl.avalara.ValidateAddressResponse
import com.junbo.billing.spec.error.AppErrors
import com.fasterxml.jackson.databind.ObjectMapper
import com.junbo.langur.core.client.MessageTranscoder
import com.ning.http.client.Response
import com.junbo.billing.clientproxy.TaxFacade
import com.junbo.billing.clientproxy.impl.avalara.AvalaraConfiguration
import com.junbo.billing.clientproxy.impl.avalara.DetailLevel
import com.junbo.billing.clientproxy.impl.avalara.GetTaxRequest
import com.junbo.billing.clientproxy.impl.avalara.GetTaxResponse
import com.junbo.billing.clientproxy.impl.avalara.Line
import com.junbo.billing.clientproxy.impl.avalara.AvalaraAddress
import com.junbo.billing.clientproxy.impl.avalara.SeverityLevel
import com.junbo.billing.clientproxy.impl.avalara.TaxDetail
import com.junbo.billing.clientproxy.impl.avalara.TaxLine
import com.junbo.billing.spec.enums.TaxAuthority
import com.junbo.billing.spec.enums.TaxStatus
import com.junbo.billing.spec.model.Balance
import com.junbo.billing.spec.model.BalanceItem
import com.junbo.billing.spec.model.ShippingAddress
import com.junbo.billing.spec.model.TaxItem
import com.junbo.langur.core.promise.Promise
import com.junbo.payment.spec.model.Address
import com.ning.http.client.AsyncHttpClient
import com.ning.http.client.AsyncHttpClient.BoundRequestBuilder
import groovy.transform.CompileStatic

import javax.annotation.Resource

/**
 * Created by LinYi on 14-3-10.
 */
@CompileStatic
class AvalaraFacadeImpl implements TaxFacade {
    @Resource(name = 'avalaraConfiguration')
    AvalaraConfiguration configuration

    @Resource(name = 'billingAsyncHttpClient')
    AsyncHttpClient asyncHttpClient

    @Resource(name = 'transcoder')
    MessageTranscoder transcoder

    static final int STATUS_CODE_MASK = 100
    static final int SUCCESSFUL_STATUS_CODE_PREFIX = 2
    static final String[] SUPPORT_COUNTRY_LIST = ['US', 'CA']

    private static final Logger LOGGER = LoggerFactory.getLogger(AvalaraFacadeImpl)


    @Override
    Promise<Balance> calculateTax(Balance balance, ShippingAddress shippingAddress, Address piAddress) {
        GetTaxRequest request = generateGetTaxRequest(balance, shippingAddress, piAddress)
        LOGGER.info('name=Get_Tax_Request, request={}', request.toString())
        return calculateTax(request).then { GetTaxResponse response ->
            return Promise.pure(updateBalance(response, balance))
        }
    }

    @Override
    Promise<ShippingAddress> validateShippingAddress(ShippingAddress shippingAddress) {
        if (SUPPORT_COUNTRY_LIST.contains(shippingAddress.country)) {
            AvalaraAddress address = getAvalaraAddress(shippingAddress)
            LOGGER.info('name=Validate_Address_Request, request={}', address.toString())
            return validateAddress(address).then { ValidateAddressResponse response ->
                return Promise.pure(updateShippingAddress(response, shippingAddress))
            }
        }

        return Promise.pure(shippingAddress)
    }

    @Override
    Promise<Address> validatePiAddress(Address piAddress) {
        if (SUPPORT_COUNTRY_LIST.contains(piAddress.country.trim().toUpperCase())) {
            AvalaraAddress address = getAvalaraAddress(piAddress)
            LOGGER.info('name=Validate_Address_Request, request={}', address.toString())
            return validateAddress(address).then { ValidateAddressResponse response ->
                return Promise.pure(updatePiAddress(response, piAddress))
            }
        }

        return Promise.pure(piAddress)
    }

    ShippingAddress updateShippingAddress(ValidateAddressResponse response, ShippingAddress shippingAddress) {
        if (response != null && response.resultCode == SeverityLevel.Success) {
            shippingAddress.street = response.address.line1
            shippingAddress.street1 = response.address.line2
            shippingAddress.street2 = response.address.line3
            shippingAddress.city = response.address.city
            shippingAddress.state = response.address.region
            shippingAddress.postalCode = response.address.postalCode
            shippingAddress.country = response.address.country
        } else {
            LOGGER.error('name=Address_Validation_Response_Invalid.')
            throw AppErrors.INSTANCE.addressValidationError('Invalid response.').exception()
        }
        return shippingAddress
    }

    Address updatePiAddress(ValidateAddressResponse response, Address piAddress) {
        if (response != null && response.resultCode == SeverityLevel.Success) {
            piAddress.addressLine1 = response.address.line1
            piAddress.addressLine2 = response.address.line2
            piAddress.addressLine3 = response.address.line3
            piAddress.city = response.address.city
            piAddress.state = response.address.region
            piAddress.postalCode = response.address.postalCode
            piAddress.country = response.address.country
        } else {
            LOGGER.error('name=Address_Validation_Response_Invalid.')
            throw AppErrors.INSTANCE.addressValidationError('Invalid response.').exception()
        }
        return piAddress
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

    Promise<ValidateAddressResponse> validateAddress(AvalaraAddress address) {
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
            if (response.statusCode / STATUS_CODE_MASK == SUCCESSFUL_STATUS_CODE_PREFIX) {
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
            if (response.statusCode / STATUS_CODE_MASK == SUCCESSFUL_STATUS_CODE_PREFIX) {
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

    GetTaxRequest generateGetTaxRequest(Balance balance, ShippingAddress shippingAddress, Address piAddress) {
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

    AvalaraAddress getAvalaraAddress(ShippingAddress shippingAddress) {
        def address = new AvalaraAddress()
        if (shippingAddress.addressId != null) {
            address.addressCode = shippingAddress.addressId.value
        }
        else {
            address.addressCode = '0'
        }
        address.line1 = shippingAddress.street
        address.line2 = shippingAddress.street1
        address.line3 = shippingAddress.street2
        address.city = shippingAddress.city
        address.region = shippingAddress.state
        address.postalCode = shippingAddress.postalCode
        address.country = shippingAddress.country

        return address
    }

    AvalaraAddress getAvalaraAddress(Address piAddress) {
        def address = new AvalaraAddress()
        if (piAddress.id != null) {
            address.addressCode = piAddress.id
        }
        else {
            address.addressCode = '0'
        }
        address.line1 = piAddress.addressLine1
        address.line2 = piAddress.addressLine2
        address.line3 = piAddress.addressLine3
        address.city = piAddress.city
        address.region = piAddress.state
        address.postalCode = piAddress.postalCode
        address.country = piAddress.country

        return address
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
