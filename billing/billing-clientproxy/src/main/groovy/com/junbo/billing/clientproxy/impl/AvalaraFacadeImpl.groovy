/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.clientproxy.impl

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.junbo.billing.clientproxy.AvalaraFacade
import com.junbo.billing.clientproxy.PaymentFacade
import com.junbo.billing.clientproxy.impl.avalara.AvalaraConfiguration
import com.junbo.billing.clientproxy.impl.avalara.DetailLevel
import com.junbo.billing.clientproxy.impl.avalara.GetTaxRequest
import com.junbo.billing.clientproxy.impl.avalara.GetTaxResponse
import com.junbo.billing.clientproxy.impl.avalara.Line
import com.junbo.billing.clientproxy.impl.avalara.AvalaraAddress
import com.junbo.billing.clientproxy.impl.avalara.SeverityLevel
import com.junbo.billing.clientproxy.impl.avalara.TaxLine
import com.junbo.billing.spec.error.AppErrors
import com.junbo.billing.spec.model.Balance
import com.junbo.billing.spec.model.BalanceItem
import com.junbo.billing.spec.model.ShippingAddress
import com.junbo.billing.spec.model.TaxItem
import com.junbo.payment.spec.model.Address
import groovy.transform.CompileStatic

import javax.annotation.Resource

/**
 * Created by LinYi on 14-3-10.
 */
@CompileStatic
class AvalaraFacadeImpl implements AvalaraFacade {
    @Resource(name = 'avalaraConfiguration')
    AvalaraConfiguration configuration

    @Resource(name = 'paymentFacade')
    PaymentFacade paymentFacade

    @Override
    Balance calculateTax(Balance balance, ShippingAddress shippingAddress, Address piAddress) {
        GetTaxRequest request = generateGetTaxRequest(balance, shippingAddress, piAddress)
        GetTaxResponse response = calculateTax(request)
        updateBalance(response, balance)
    }

    Balance updateBalance(GetTaxResponse response, Balance balance) {
        balance.taxAmount = response.totalTax
        if (response.resultCode == SeverityLevel.Success) {
            balance.balanceItems.each { BalanceItem item ->
                response.taxLines.each { TaxLine line ->
                    if (item.balanceItemId.value == Long.valueOf(line.lineNo)) {
                        def taxItem = new TaxItem()
                        taxItem.taxAmount = BigDecimal.valueOf(line.tax)
                        taxItem.taxRate = BigDecimal.valueOf(line.rate)
                        item.addTaxItem(taxItem)
                    }
                }
            }

            return balance
        }
    }

    GetTaxResponse calculateTax(GetTaxRequest request) {
        // TODO: call avalara REST API, use HttpURLConnection for now
        String getTaxUrl = configuration.baseUrl + 'tax/get'
        URL url
        HttpURLConnection connection
        try {
            url = new URL(getTaxUrl)
            connection = (HttpURLConnection)url.openConnection()
            connection.setRequestMethod('POST')
            connection.setDoOutput(true)
            connection.setDoInput(true)
            connection.setUseCaches(false)
            connection.setAllowUserInteraction(false)
            ObjectMapper mapper = new ObjectMapper()
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
            String content = mapper.writeValueAsString(request)
            connection.setRequestProperty('Content-Length', content.size().toString())
            connection.setRequestProperty('Authorization', configuration.authorization)
            connection.setRequestProperty('Content-Type', 'application/x-www-form-urlencoded')
            DataOutputStream outputStream = new DataOutputStream(connection.outputStream)
            outputStream.writeBytes(content)
            outputStream.flush()
            outputStream.close()
            connection.disconnect()
            GetTaxResponse response = mapper.readValue(connection.inputStream, GetTaxResponse)

            if (connection.responseCode != 200) {
                // TODO: error handling
                return response
            }
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            return response
        } catch (IOException e) {
            throw AppErrors.INSTANCE.taxCalculationError('Fail to connect to avalara server.').exception()
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
        shipToAddress.addressCode = 'ShipToAddress'
        shipToAddress.line1 = shippingAddress.street
        shipToAddress.line2 = shippingAddress.street1
        shipToAddress.line3 = shippingAddress.street2
        shipToAddress.city = shippingAddress.city
        shipToAddress.region = shippingAddress.state
        shipToAddress.postalCode = shippingAddress.postalCode
        shipToAddress.country = shippingAddress.country
        addresses << shipToAddress

        AvalaraAddress billToAddress = null
        if (piAddress != null) {
            billToAddress = new AvalaraAddress()
            billToAddress.addressCode = 'BillToAddress'
            billToAddress.line1 = piAddress.addressLine1
            billToAddress.line2 = piAddress.addressLine2
            billToAddress.line3 = piAddress.addressLine3
            billToAddress.city = piAddress.city
            billToAddress.region = piAddress.state
            billToAddress.postalCode = piAddress.postalCode
            billToAddress.country = piAddress.country
            addresses << billToAddress
        }
        request.addresses = addresses

        // lines
        def lines = []
        balance.balanceItems.each { BalanceItem item ->
            def line = new Line()
            line.lineNo = item.balanceItemId.value.toString()
            // TODO: confirm address collection
            line.destinationCode = shipToAddress.addressCode
            line.originCode = billToAddress == null ? shipToAddress.addressCode: billToAddress.addressCode
            line.qty = BigDecimal.valueOf(1)
            line.amount = item.amount
            line.itemCode = item.balanceItemId.value.toString()
            line.taxIncluded = item.taxIncluded
            lines << line
        }

        request.lines = lines
        return request
    }
}
