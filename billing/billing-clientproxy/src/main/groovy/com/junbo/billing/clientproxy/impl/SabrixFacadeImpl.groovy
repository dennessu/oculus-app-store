/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.clientproxy.impl

import static com.ning.http.client.extra.ListenableFutureAdapter.asGuavaFuture

import com.junbo.billing.clientproxy.impl.sabrix.Batch
import com.junbo.billing.clientproxy.impl.sabrix.Invoice
import com.junbo.billing.clientproxy.impl.sabrix.Line
import com.junbo.billing.clientproxy.impl.sabrix.SabrixConfiguration
import com.junbo.billing.clientproxy.impl.sabrix.Tax
import com.junbo.billing.spec.enums.PropertyKey
import com.junbo.billing.spec.enums.TaxAuthority
import com.junbo.billing.spec.enums.TaxStatus
import com.junbo.billing.spec.model.BalanceItem
import com.junbo.billing.spec.model.TaxItem
import com.junbo.billing.clientproxy.impl.sabrix.Message
import com.junbo.billing.spec.error.AppErrors
import com.ning.http.client.Response
import com.junbo.billing.clientproxy.TaxFacade
import com.junbo.billing.clientproxy.impl.sabrix.AddressValidationResponse
import com.junbo.billing.clientproxy.impl.sabrix.ResponseAddress
import com.junbo.billing.clientproxy.impl.sabrix.SabrixAddress
import com.junbo.billing.spec.model.Balance
import com.junbo.common.enumid.CountryId
import com.junbo.identity.spec.v1.model.Address
import com.junbo.langur.core.promise.Promise
import com.ning.http.client.AsyncHttpClient
import com.thoughtworks.xstream.XStream
import com.ning.http.client.AsyncHttpClient.BoundRequestBuilder
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.annotation.Resource

/**
 * Implementation of Sabrix facade to calculate tax & validate address.
 */
@CompileStatic
class SabrixFacadeImpl implements TaxFacade {
    @Resource(name = 'billingAsyncHttpClient')
    AsyncHttpClient asyncHttpClient

    @Resource(name = 'sabrixConfiguration')
    SabrixConfiguration configuration

    static final int STATUS_CODE_MASK = 100
    static final int SUCCESSFUL_STATUS_CODE_PREFIX = 2
    static final int SUCCESSFUL_PROCESSING = 0
    private static final Logger LOGGER = LoggerFactory.getLogger(SabrixFacadeImpl)

    @Override
    Promise<Balance> calculateTaxQuote(Balance balance, Address shippingAddress, Address piAddress) {
        Batch batch = generateBatch(balance, shippingAddress, piAddress, false)
        LOGGER.info('name=Tax_Calculation_Batch, batch={}', batch.toString())
        return calculateTax(batch).then { Batch result ->
            return Promise.pure(updateBalance(result, balance))
        }
    }

    @Override
    Promise<Balance> calculateTax(Balance balance, Address shippingAddress, Address piAddress) {
        Batch batch = generateBatch(balance, shippingAddress, piAddress, true)
        LOGGER.info('name=Tax_Calculation_Batch, batch={}', batch.toString())
        return calculateTax(batch).then { Batch result ->
            return Promise.pure(updateBalance(result, balance))
        }
    }

    @Override
    Promise<Address> validateAddress(Address address) {
        SabrixAddress externalAddress = getSabrixAddress(address)
        LOGGER.info('name=Validate_Address_Request, address={}', externalAddress.toString())
        return validateSabrixAddress(externalAddress).then { AddressValidationResponse response ->
            return Promise.pure(updateAddress(response, address))
        }
    }

    Batch generateBatch(Balance balance, Address shippingAddress, Address piAddress, boolean isAudited) {
        Batch batch = new Batch()
        batch.companyRole = configuration.companyRole
        batch.externalCompanyId = configuration.externalCompanyId
        batch.username = configuration.username
        batch.password = configuration.password
        def invoices = []
        invoices << generateInvoice(balance, shippingAddress, piAddress, isAudited)
        batch.invoice = invoices
        return batch
    }

    Invoice generateInvoice(Balance balance, Address shippingAddress, Address piAddress, boolean isAudited) {
        Invoice invoice = new Invoice()
        invoice.invoiceNumber = balance.balanceId?.value
        invoice.invoiceDate = new Date()
        invoice.currencyCode = balance.currency
        invoice.isAudited = isAudited
        SabrixAddress billToAddress = toSabrixAddress(piAddress)
        SabrixAddress shipToAddress = toSabrixAddress(shippingAddress)
        invoice.billTo = billToAddress
        invoice.shipTo = shipToAddress
        def lines = generateLine(balance, billToAddress, shipToAddress)
        invoice.line = lines

        return invoice
    }

    List<Line> generateLine(Balance balance, SabrixAddress billToAddress, SabrixAddress shipToAddress) {
        def lines = []
        balance.balanceItems.eachWithIndex { BalanceItem item, int index ->
            Line line = new Line()
            line.id = index
            line.lineNumber = index
            line.grossAmount = item.amount.toDouble()
            line.productCode = item.financeId
            line.transactionType = getTransactionType(item)
            line.billTo = billToAddress
            if (line.transactionType == 'GS') {
                line.shipTo = shipToAddress
                line.shipFrom = getSabrixShipFromAddress()
            }
            lines << line
        }
        return lines
    }

    String getTransactionType(BalanceItem item) {
        // 'GS'(Goods) for physical goods, and 'ES'(Electronic Services) otherwise.
        if (item.propertySet == null) {
            return 'ES'
        }
        String type = item.propertySet.get(PropertyKey.ITEM_TYPE.name())
        switch (type) {
            case 'PHYSICAL':
                return 'GS'
            default:
                return 'ES'
        }
    }

    SabrixAddress toSabrixAddress(Address address) {
        if (address == null) {
            return null
        }
        SabrixAddress sabrixAddress = new SabrixAddress()
        sabrixAddress.country = address.countryId.value
        switch (sabrixAddress.country) {
            case 'US':
                sabrixAddress.state = address.subCountry
                sabrixAddress.city = address.city
                sabrixAddress.postcode = address.postalCode
                break
            case 'CA':
                sabrixAddress.province = address.subCountry
                break
            default:
                break
        }
        return sabrixAddress
    }

    SabrixAddress getSabrixShipFromAddress() {
        SabrixAddress sabrixAddress = new SabrixAddress()
        sabrixAddress.country = configuration.shipFromCity
        sabrixAddress.state = configuration.shipFromState
        sabrixAddress.city = configuration.shipFromCity
        sabrixAddress.postcode = configuration.shipFromPostalCode

        return sabrixAddress
    }

    Promise<Batch> calculateTax(Batch batch) {
        // TODO: call sabrix to calculate tax
        return null
    }

    Balance updateBalance(Batch result, Balance  balance) {
        if (result.requestStatus?.isSuccess || result.requestStatus?.isPartialSuccess) {
            Invoice resultInvoice = result.invoice[0]
            balance.taxAmount = resultInvoice.totalTaxAmount
            balance.taxIncluded = resultInvoice?.calculationDirection.equalsIgnoreCase('T')
            balance.balanceItems.eachWithIndex { BalanceItem item, int index ->
                resultInvoice.line.each { Line line ->
                    if (index == line.lineNumber) {
                        item.taxAmount = line.totalTaxAmount
                        line.tax.each { Tax tax ->
                            def taxItem = new TaxItem()
                            taxItem.taxAmount = BigDecimal.valueOf(tax.taxAmount.documentAmount)
                            taxItem.taxRate = BigDecimal.valueOf(tax.taxRate)
                            taxItem.taxAuthority = getTaxAuthority(tax.authorityType)
                            item.addTaxItem(taxItem)
                        }
                    }
                }
            }
            balance.setTaxStatus(TaxStatus.TAXED.name())
        }
        else {
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

    SabrixAddress getSabrixAddress(Address address) {
        /*
        For US addresses: <COUNTRY> + <POSTCODE>
        For Non-US address: <COUNTRY>
         */
        def sabrixAddress = new SabrixAddress()
        sabrixAddress.country = address.countryId.value
        if (sabrixAddress.country == 'US') {
            sabrixAddress.postcode = address.postalCode
        }
        return sabrixAddress
    }

    Promise<AddressValidationResponse> validateSabrixAddress(SabrixAddress address) {
        // TODO: UPDATE URL
        String validateAddressUrl = 'localhost'
        XStream xstream = new XStream()
        xstream.autodetectAnnotations(true)
        String content = xstream.toXML(address)
        def requestBuilder = buildRequest(validateAddressUrl, content)
        return Promise.wrap(asGuavaFuture(requestBuilder.execute())).recover { Throwable throwable ->
            LOGGER.error('Error_Build_Sabrix_Request.', throwable)
            throw AppErrors.INSTANCE.addressValidationError('Fail to build request.').exception()
        }.then { Response response ->
            AddressValidationResponse addressValidationResponse =
                    (AddressValidationResponse)xstream.fromXML(response.responseBody)
            if (addressValidationResponse == null) {
                LOGGER.error('name=Error_Read_Sabrix_Response.')
                throw AppErrors.INSTANCE.addressValidationError('Fail to read response.').exception()
            }
            if (response.statusCode / STATUS_CODE_MASK == SUCCESSFUL_STATUS_CODE_PREFIX) {
                LOGGER.info('name=Address_Validation_Response, response={}', addressValidationResponse.toString())
                return Promise.pure(addressValidationResponse)
            }
            LOGGER.error('name=Error_Address_Validation.')
            LOGGER.info('name=Address_Validation_Response_Status_Code, statusCode={}', response.statusCode)
            String detail = ''
            addressValidationResponse.message.each { Message message ->
                if (message.severity > SUCCESSFUL_PROCESSING) {
                    LOGGER.info('name=Address_Validation_Response_Error_Message, message={}', message.messageText)
                    detail += message.messageText
                }
            }
            throw AppErrors.INSTANCE.addressValidationError(detail).exception()
        }
    }

    BoundRequestBuilder buildRequest(String url, String content) {
        // TODO: Authentication
        def requestBuilder = asyncHttpClient.preparePost(url)
        requestBuilder.setBody(content)
        requestBuilder.setUrl(url)
        return requestBuilder
    }

    Address updateAddress(AddressValidationResponse response, Address address) {
        def validatedAddress = response.address.find { ResponseAddress responseAddress ->
            responseAddress.country?.name != 'US' || responseAddress.city?.name.equalsIgnoreCase(address.city)
        }
        if (validatedAddress == null) {
            validatedAddress = response.address[0]
        }

        address.countryId = new CountryId(validatedAddress.country.code)
        address.city = validatedAddress.city?.name
        address.postalCode = validatedAddress.postcode?.name
        address.subCountry = validatedAddress.state != null ?
                validatedAddress.state.code : validatedAddress.province?.code
        return address
    }
}
