/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.clientproxy.impl

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
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
import com.junbo.payment.spec.model.Address
import groovy.transform.CompileStatic

import javax.annotation.Resource

/**
 * Created by LinYi on 14-3-10.
 */
@CompileStatic
class AvalaraFacadeImpl implements TaxFacade {
    @Resource(name = 'avalaraConfiguration')
    AvalaraConfiguration configuration

    @Override
    Balance calculateTax(Balance balance, ShippingAddress shippingAddress, Address piAddress) {
        GetTaxRequest request = generateGetTaxRequest(balance, shippingAddress, piAddress)
        GetTaxResponse response = calculateTax(request)
        updateBalance(response, balance)
    }

    Balance updateBalance(GetTaxResponse response, Balance balance) {
        balance.taxAmount = response.totalTax
        if (response != null && response.resultCode == SeverityLevel.Success) {
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

    GetTaxResponse calculateTax(GetTaxRequest request) {
        // TODO: call avalara REST API, use HttpURLConnection for now
        String getTaxUrl = configuration.baseUrl + 'tax/get'
        URL url
        HttpURLConnection connection
        GetTaxResponse response
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
            response = mapper.readValue(connection.inputStream, GetTaxResponse)

            if (connection.responseCode != 200) {
                // TODO: error handling
                return null
            }
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            return response
        } catch (IOException e) {
            // TODO: error handling
            return response
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
            shipToAddress.addressCode = shippingAddress.addressId.value.toString()
            shipToAddress.line1 = shippingAddress.street
            shipToAddress.line2 = shippingAddress.street1
            shipToAddress.line3 = shippingAddress.street2
            shipToAddress.city = shippingAddress.city
            shipToAddress.region = shippingAddress.state
            shipToAddress.postalCode = shippingAddress.postalCode
            shipToAddress.country = shippingAddress.country
        }
        else {
            shipToAddress.addressCode = piAddress.id.toString()
            shipToAddress.line1 = piAddress.addressLine1
            shipToAddress.line2 = piAddress.addressLine2
            shipToAddress.line3 = piAddress.addressLine3
            shipToAddress.city = piAddress.city
            shipToAddress.region = piAddress.state
            shipToAddress.postalCode = piAddress.postalCode
            shipToAddress.country = piAddress.country
        }
        addresses << shipToAddress

        def shipFromAddress = new AvalaraAddress()
        shipFromAddress.addressCode = '0'
        shipFromAddress.line1 = configuration.shipFromStreet
        shipFromAddress.city = configuration.shipFromCity
        shipFromAddress.region = configuration.shipFromState
        shipFromAddress.postalCode = configuration.shipFromPostalCode
        shipFromAddress.country = configuration.shipFromCountry
        addresses << shipFromAddress
        request.addresses = addresses

        // lines
        def lines = []
        balance.balanceItems.eachWithIndex { BalanceItem item, int index ->
            def line = new Line()
            line.lineNo = index.toString()
            // TODO: confirm address collection
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
}
