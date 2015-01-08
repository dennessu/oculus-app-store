/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.clientproxy.impl

import com.junbo.billing.clientproxy.TaxFacade
import com.junbo.billing.clientproxy.impl.avalara.ResponseMessage
import com.junbo.billing.clientproxy.impl.common.VatUtil
import com.junbo.billing.clientproxy.impl.common.XmlConvertor
import com.junbo.billing.clientproxy.impl.sabrix.*
import com.junbo.billing.spec.enums.BalanceType
import com.junbo.billing.spec.enums.PropertyKey
import com.junbo.billing.spec.enums.TaxAuthority
import com.junbo.billing.spec.enums.TaxStatus
import com.junbo.billing.spec.error.AppErrors
import com.junbo.billing.spec.model.Balance
import com.junbo.billing.spec.model.BalanceItem
import com.junbo.billing.spec.model.TaxItem
import com.junbo.billing.spec.model.VatIdValidationResponse
import com.junbo.common.enumid.CountryId
import com.junbo.common.error.ErrorDetail
import com.junbo.identity.spec.v1.model.Address
import com.junbo.langur.core.async.JunboAsyncHttpClient
import com.junbo.langur.core.async.JunboAsyncHttpClient.BoundRequestBuilder
import com.junbo.langur.core.promise.Promise
import com.ning.http.client.Response
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.util.CollectionUtils

import javax.annotation.Resource
import java.text.SimpleDateFormat

/**
 * Implementation of Sabrix facade to calculate tax & validate address.
 */
@CompileStatic
class SabrixFacadeImpl implements TaxFacade {
    @Resource(name = 'commonAsyncHttpClient')
    JunboAsyncHttpClient asyncHttpClient

    @Resource(name = 'sabrixConfiguration')
    SabrixConfiguration configuration

    @Resource(name = 'xmlConvertor')
    XmlConvertor xmlConvertor

    static final int SUCCESSFUL_PROCESSING = 0
    static final String GOODS = 'GS'
    static final String ELECTRONIC_SERVICES = 'ES'
    static final String CALCULATION_DIRECTION_FORWARD = 'F'
    static final String CALCULATION_DIRECTION_REVERSE = 'R'
    static final String CALCULATION_DIRECTION_REVERSE_FROM_TOTAL = 'T'
    static final String REFUND_PREFIX = 'CM'
    static final String TAX_STATUS_BUSINESS = 'business'
    static final String TAX_STATUS_CONSUMER = 'consumer'
    private static final Logger LOGGER = LoggerFactory.getLogger(SabrixFacadeImpl)
    private static final ThreadLocal<SimpleDateFormat> DATE_FORMATTER =
            new ThreadLocal<SimpleDateFormat>() {
                @Override
                protected SimpleDateFormat initialValue() {
                    def ret = new SimpleDateFormat('yyyy-MM-dd', Locale.US)
                    ret.timeZone = TimeZone.getTimeZone('UTC');
                    return ret
                }
            }
    private static final Map<String, TaxAuthority> AUTHORITY_MAP
    private static final Map<String, String> EXCHANGE_RATE_MAP

    static {
        Map<String, TaxAuthority> authorityMap = new HashMap<String, TaxAuthority>()
        authorityMap.put('Country', TaxAuthority.COUNTRY)
        authorityMap.put('State Sales/Use', TaxAuthority.STATE)
        authorityMap.put('County Sales/Use', TaxAuthority.COUNTY)
        authorityMap.put('City Sales/Use', TaxAuthority.CITY)
        authorityMap.put('District Sales/Use', TaxAuthority.DISTRICT)
        authorityMap.put('Territory', TaxAuthority.TERRITORY)
        authorityMap.put('GST', TaxAuthority.GST)
        authorityMap.put('HST', TaxAuthority.GST)
        authorityMap.put('PST', TaxAuthority.PST)
        authorityMap.put('QST', TaxAuthority.PST)
        authorityMap.put('IST', TaxAuthority.IST)
        authorityMap.put('IPI', TaxAuthority.IPI)
        authorityMap.put('PIS', TaxAuthority.PIS)
        authorityMap.put('COF', TaxAuthority.COF)
        authorityMap.put('ICMS', TaxAuthority.ICMS)
        authorityMap.put('ISS', TaxAuthority.ISS)
        authorityMap.put('CBT', TaxAuthority.BT)
        authorityMap.put('CSC', TaxAuthority.SURCHARGE)
        authorityMap.put('VAT', TaxAuthority.VAT)

        AUTHORITY_MAP = Collections.unmodifiableMap(authorityMap)

        Map<String, String> exchangeRateMap = new HashMap<String, String>()
        exchangeRateMap.put('AUD', '0.94')
        exchangeRateMap.put('EUR', '1.35')
        exchangeRateMap.put('BGN', '0.69')
        exchangeRateMap.put('CAD', '0.93')
        exchangeRateMap.put('HRK', '0.18')
        exchangeRateMap.put('CZK', '0.049')
        exchangeRateMap.put('DKK', '0.18')
        exchangeRateMap.put('HUF', '0.0044')
        exchangeRateMap.put('LVL', '1.93')
        exchangeRateMap.put('LTL', '0.39')
        exchangeRateMap.put('PLN', '0.33')
        exchangeRateMap.put('RON', '0.30')
        exchangeRateMap.put('SEK', '0.15')
        exchangeRateMap.put('GBP', '1.71')

        EXCHANGE_RATE_MAP = Collections.unmodifiableMap(exchangeRateMap)
    }
    @Override
    Promise<Balance> calculateTaxQuote(Balance balance, Address shippingAddress, Address piAddress) {
        Batch batch = generateBatch(balance, shippingAddress, piAddress, false)
        //LOGGER.info('name=Tax_Calculation_Quote_Batch, batch={}', batch.toString())
        return calculateTax(batch, false).then { TaxCalculationResponse result ->
            return Promise.pure(updateBalance(result, balance))
        }
    }

    @Override
    Promise<Balance> calculateTax(Balance balance, Address shippingAddress, Address piAddress) {
        Batch batch = generateBatch(balance, shippingAddress, piAddress, true)
        //LOGGER.info('name=Tax_Calculation_Batch, batch={}', batch.toString())
        return calculateTax(batch, true).then { TaxCalculationResponse result ->
            return Promise.pure(updateAuditedBalance(result, balance))
        }
    }

    @Override
    Promise<Address> validateAddress(Address address) {
        SabrixAddress externalAddress = getSabrixAddress(address)
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug('name=Validate_Address_Request, address={}', externalAddress.toString())
        }
        return validateSabrixAddress(externalAddress).then { AddressValidationResponse response ->
            return Promise.pure(updateAddress(response, address))
        }
    }

    @Override
    Promise<VatIdValidationResponse> validateVatId(String vatId, String country) {
        if (!VatUtil.isValidFormat(vatId, country)) {
            def validationResponse = new VatIdValidationResponse()
            validationResponse.vatId = vatId
            validationResponse.message = 'Invalid format.'
            validationResponse.status = 'INVALID'
            return Promise.pure(validationResponse)
        }
        RegistrationValidationRequest request = generateRequest(vatId)
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug('name=Registration_Validation_Request, request={}', request.toString())
        }
        return validateVatId(request).then { RegistrationValidationResponse response ->
            return getVatIdValidationResponse(response)
        }
    }

    RegistrationValidationRequest generateRequest(String vatId) {
        def request = new RegistrationValidationRequest()
        request.registration = [vatId]

        return request
    }

    Promise<VatIdValidationResponse> getVatIdValidationResponse(RegistrationValidationResponse response) {
        def validationResponse = new VatIdValidationResponse()
        if (response.registration == null || response.registration.size() == 0) {
            validationResponse.status = 'FAILED'
            validationResponse.message = ''
        }
        else {
            def registration = response.registration[0]
            validationResponse.message = registration.message
            validationResponse.vatId = registration.registration
            switch (registration.message) {
                case 'Yes, valid VAT number.' :
                    validationResponse.status = 'VALID'
                    validationResponse.companyName = registration.name
                    validationResponse.address = registration.address
                    break
                case 'Error: Incomplete (VAT + Member State) or corrupted data input.' :
                case 'No, invalid VAT number.' :
                    validationResponse.status = 'INVALID'
                    break
                case 'Service unavailable. Please re-submit your request later.' :
                case 'Member state service unavailable. Please re-submit your request later.' :
                case 'Request time-out. Please re-submit your request later.' :
                    validationResponse.status = 'SERVICE_UNAVAILABLE'
                    break
                default :
                    validationResponse.status = 'UNKNOWN'
            }
        }
        return Promise.pure(validationResponse)
    }

    Batch generateBatch(Balance balance, Address shippingAddress, Address piAddress, boolean isAudited) {
        Batch batch = new Batch()
        batch.username = configuration.username
        batch.password = configuration.password
        batch.version = configuration.version
        def invoices = []
        invoices << generateInvoice(balance, shippingAddress, piAddress, isAudited)
        batch.invoice = invoices
        return batch
    }

    Invoice generateInvoice(Balance balance, Address shippingAddress, Address piAddress, boolean isAudited) {
        Invoice invoice = new Invoice()
        if (!CollectionUtils.isEmpty(balance.orderIds)) {
            if (balance.orderIds.size() > 1) {
                LOGGER.error('Error_More_Than_One_Order_In_Tax_Calculation.')
                throw AppErrors.INSTANCE.taxCalculationError('Multi-order tax calculation is not supported.').exception()
            }
            // combination of hostSystem, callingSystemNumber and uniqueInvoiceNumber makes a audit key
            if (balance.orderIds[0] != null) {
                invoice.hostSystem = configuration.hostSystem
                invoice.callingSystemNumber = configuration.callingSystemNumber
                invoice.uniqueInvoiceNumber = getUniqueInvoiceNumber(balance)
                invoice.invoiceNumber = balance.orderIds?.get(0)?.value
            }
        }
        invoice.deliveryTerms = DeliveryTerm.DDP.name()
        invoice.companyRole = configuration.companyRole
        invoice.externalCompanyId = configuration.externalCompanyId
        boolean isRefund = BalanceType.REFUND.name() == balance.type
        if (isRefund && isAudited) {
            invoice.originalInvoiceNumber = balance.orderIds?.get(0)?.value
            invoice.originalInvoiceDate = balance.propertySet.get(PropertyKey.INVOICE_DATE.name())
        }
        invoice.calculationDirection = isRefund && isAudited ?
                CALCULATION_DIRECTION_REVERSE : CALCULATION_DIRECTION_FORWARD
        if (isAudited) {
            invoice.invoiceDate = balance.propertySet.get(PropertyKey.INVOICE_DATE.name())
        }
        else {
            String invoiceDate = DATE_FORMATTER.get().format(new Date())
            invoice.invoiceDate = invoiceDate
            balance.propertySet.put(PropertyKey.INVOICE_DATE.name(), invoiceDate)
        }
        invoice.currencyCode = balance.currency
        invoice.isAudited = isAudited
        invoice.customerName = balance.propertySet.get(PropertyKey.CUSTOMER_NAME.name())
        invoice.customerNumber = balance.userId.value.toString()
        SabrixAddress billToAddress = toSabrixAddress(piAddress)
        SabrixAddress shipToAddress
        if (shippingAddress != null) {
            shipToAddress = toSabrixAddress(shippingAddress)
        }
        else {
            shipToAddress = billToAddress
        }
        invoice.sellerPrimary = new SabrixAddress()
        invoice.sellerPrimary.country = getSellerPrimaryCountry(balance, billToAddress, shipToAddress)
        invoice.buyerPrimary = new SabrixAddress()
        invoice.buyerPrimary.country = billToAddress.country
//        invoice.billTo = billToAddress
//        invoice.shipTo = shipToAddress
//        invoice.shipFrom = getSabrixShipFromAddress()
        def lines = generateLine(balance, billToAddress, shipToAddress, isAudited)
        invoice.line = lines
        setupUserElement(invoice, balance)

        return invoice
    }

    List<Line> generateLine(Balance balance, SabrixAddress billToAddress,
                            SabrixAddress shipToAddress, boolean isAudited) {
        def lines = []
        def isRefund = BalanceType.REFUND.name() == balance.type
        balance.balanceItems.eachWithIndex { BalanceItem item, int index ->
            Line line = new Line()
            line.id = index + 1
            line.lineNumber = line.id
            line.grossAmount = item.amount?.toDouble()
            if (isRefund && isAudited) {
                line.taxAmount = item.taxAmount?.toDouble()
            }
            if (!configuration.taxExclusiveCountries.contains(shipToAddress.country)) {
                line.inclusiveTaxIndicator = new InclusiveTaxIndicator(
                        fullyInclusive: true
                )
            }
            line.discountAmount = item.discountAmount?.toDouble()
            line.transactionType = getTransactionType(item)
            line.productCode = item.propertySet.get(PropertyKey.ITEM_TYPE.name())?.replace('_', ' ')
            line.description = item.propertySet.get(PropertyKey.ITEM_DESCRIPTION.name())
            line.partNumber = item.propertySet.get(PropertyKey.ORDER_ITEM_ID.name())
            if (item.propertySet.get(PropertyKey.VAT_ID.name()) != null) {
                def registrations = new Registrations()
                registrations.buyerRole = item.propertySet.get(PropertyKey.VAT_ID.name())
                line.registrations = registrations
            }
            line.vendorNumber = item.propertySet.get(PropertyKey.ORGANIZATION_ID.name())
            line.vendorName = item.propertySet.get(PropertyKey.VENDOR_NAME.name())
            line.billTo = billToAddress
            line.shipTo = shipToAddress
            if (line.transactionType == GOODS) {
                line.shipFrom = getSabrixShipFromAddress(shipToAddress)
            }
            setupUserElement(line, item)
            lines << line
        }

        return lines
    }

    String getSellerPrimaryCountry(Balance balance, SabrixAddress billToAddress, SabrixAddress shipToAddress) {
        boolean hasPhysical = balance.balanceItems.any { BalanceItem item ->
            getTransactionType(item) == GOODS
        }
        if (hasPhysical && shipToAddress != null) {
            if (configuration.VATRegistrationCountries.contains(shipToAddress.country)) {
                return shipToAddress.country
            } else {
                Warehouse warehouse = getWarehouse(shipToAddress.country)
                if (warehouse.isEUCountry) {
                    return warehouse.location
                }
                else {
                    return 'US'
                }
            }
        }
        else {
            configuration.VATRegistrationCountries.contains(billToAddress.country) ?
                    billToAddress.country : 'US'
        }
    }

    Warehouse getWarehouse(String country) {
        Warehouse w = EnumSet.allOf(Warehouse).find { Warehouse warehouse ->
            warehouse.shipCountryList.contains(country)
        }
        return w != null ? w : Warehouse.WAREHOUSE_US
    }

    String getUniqueInvoiceNumber(Balance balance) {
        String uniqueInvoiceNumber = balance.orderIds[0]?.value.toString()
        if (balance.id != null) {
            uniqueInvoiceNumber += '_' + balance.getId().value.toString()
        }
        if (BalanceType.REFUND.name() == balance.type) {
            return REFUND_PREFIX + '_' + uniqueInvoiceNumber
        }
        return uniqueInvoiceNumber
    }

    void setupUserElement(Invoice invoice, Balance balance) {
        invoice.userElement = new ArrayList<UserElement>()
        if (balance.propertySet.get(PropertyKey.IP_GEO_LOCATION.name()) != null) {
            UserElement attribute1 = new UserElement()
            attribute1.name = 'ATTRIBUTE1'
            attribute1.value = balance.propertySet.get(PropertyKey.IP_GEO_LOCATION.name())
            invoice.userElement.add(attribute1)
        }
        if (balance.propertySet.get(PropertyKey.PI_ADDRESS.name()) != null) {
            UserElement attribute2 = new UserElement()
            attribute2.name = 'ATTRIBUTE2'
            attribute2.value = balance.propertySet.get(PropertyKey.PI_ADDRESS.name())
            invoice.userElement.add(attribute2)
        }

        UserElement attribute3 = new UserElement()
        attribute3.name = 'ATTRIBUTE3'
        if (balance.balanceItems.any { BalanceItem balanceItem ->
            balanceItem.propertySet.get(PropertyKey.VAT_ID.name()) != null
        }) {
            attribute3.value = TAX_STATUS_BUSINESS
        }
        else {
            attribute3.value = TAX_STATUS_CONSUMER
        }
        invoice.userElement.add(attribute3)

        if (balance.propertySet.get(PropertyKey.PAYMENT_METHOD.name()) != null) {
            UserElement attribute4 = new UserElement()
            attribute4.name = 'ATTRIBUTE4'
            attribute4.value = balance.propertySet.get(PropertyKey.PAYMENT_METHOD.name())
            invoice.userElement.add(attribute4)
        }
        if (EXCHANGE_RATE_MAP.get(balance.currency) != null) {
            UserElement attribute5 = new UserElement()
            attribute5.name = 'ATTRIBUTE5'
            attribute5.value = EXCHANGE_RATE_MAP.get(balance.currency)
            invoice.userElement.add(attribute5)
        }
        if (balance.propertySet.get(PropertyKey.IP_ADDRESS.name()) != null) {
            UserElement attribute6 = new UserElement()
            attribute6.name = 'ATTRIBUTE6'
            attribute6.value = balance.propertySet.get(PropertyKey.IP_ADDRESS.name())
            invoice.userElement.add(attribute6)
        }
        if (balance.propertySet.get(PropertyKey.BIN_NUMBER.name()) != null) {
            UserElement attribute7 = new UserElement()
            attribute7.name = 'ATTRIBUTE7'
            attribute7.value = balance.propertySet.get(PropertyKey.BIN_NUMBER.name())
            invoice.userElement.add(attribute7)
        }
        if (balance.propertySet.get(PropertyKey.BIN_COUNTRY.name()) != null) {
            UserElement attribute8 = new UserElement()
            attribute8.name = 'ATTRIBUTE8'
            attribute8.value = balance.propertySet.get(PropertyKey.BIN_COUNTRY.name())
            invoice.userElement.add(attribute8)
        }
    }

    void setupUserElement(Line line, BalanceItem item) {
        line.userElement = new ArrayList<UserElement>()
        if (item.propertySet.get(PropertyKey.OFFER_ID.name()) != null) {
            UserElement attribute1 = new UserElement()
            attribute1.name = 'ATTRIBUTE1'
            attribute1.value = item.propertySet.get(PropertyKey.OFFER_ID.name())
            line.userElement.add(attribute1)
        }
        if (item.propertySet.get(PropertyKey.ITEM_NAME.name()) != null) {
            UserElement attribute2 = new UserElement()
            attribute2.name = 'ATTRIBUTE2'
            attribute2.value = item.propertySet.get(PropertyKey.ITEM_NAME.name())
            line.userElement.add(attribute2)
        }
    }

    Entity getEntity(Address piAddress) {
        Entity entity = EnumSet.allOf(Entity).find { Entity entity ->
            entity.country == piAddress.countryId.value
        }
        if (entity == null) {
            return Entity.US_ENTITY
        }

        return entity
    }

    String getTransactionType(BalanceItem item) {
        // 'GS'(Goods) for physical goods, and 'ES'(Electronic Services) otherwise.
        if (item.propertySet == null) {
            return ELECTRONIC_SERVICES
        }
        String type = item.propertySet.get(PropertyKey.ITEM_TYPE.name())
        switch (type) {
            case 'PHYSICAL_GOODS':
                return GOODS
            default:
                return ELECTRONIC_SERVICES
        }
    }

    SabrixAddress toSabrixAddress(Address address) {
        if (address == null) {
            return null
        }
        SabrixAddress sabrixAddress = new SabrixAddress()
        sabrixAddress.country = address.countryId.value
        sabrixAddress.city = address.city
        switch (sabrixAddress.country) {
            case 'US':
                sabrixAddress.state = address.subCountry
                sabrixAddress.postcode = address.postalCode
                break
            case 'CA':
                sabrixAddress.province = address.subCountry
                sabrixAddress.postcode = address.postalCode
                break
            default:
                break
        }
        return sabrixAddress
    }

    SabrixAddress getSabrixShipFromAddress(SabrixAddress shipToAddress) {
        SabrixAddress sabrixAddress = new SabrixAddress()
        Warehouse warehouse = getWarehouse(shipToAddress.country)
        sabrixAddress.country = warehouse.location
        return sabrixAddress
    }

    Promise<TaxCalculationResponse> calculateTax(Batch batch, boolean isAudited) {
        String taxCalculationUrl
        if (isAudited) {
            taxCalculationUrl = configuration.taxAuditUrl + 'sabrix/xmlinvoice'
        }
        else {
            taxCalculationUrl = configuration.baseUrl + 'sabrix/xmlinvoice'
        }
        String content = xmlConvertor.getXml(batch)
        def requestBuilder = buildRequest(taxCalculationUrl, content)
        return requestBuilder.execute().recover { Throwable throwable ->
            LOGGER.error('Error_Build_Sabrix_Request.', throwable)
            return Promise.pure(null)
        }.then { Response response ->
            TaxCalculationResponse result = null
            if (response != null && response.responseBody != null) {
                result = xmlConvertor.getTaxCalculationResponse(response.responseBody)
            }
            if (result == null) {
                LOGGER.error('name=Error_Read_Sabrix_Tax_Calculation_Response.')
                return Promise.pure(null)
            }
            if (response.statusCode / 100 == 2) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug('name=Tax_Calculation_Response, response={}', result.toString())
                }
                return Promise.pure(result)
            }
            LOGGER.error('name=Error_Tax_Calculation, description={}', result.requestStatus?.error?.description)
            LOGGER.info('name=Tax_Calculation_Response_Status_Code, statusCode={}', response.statusCode)
            return Promise.pure(null)
        }
    }

    Balance updateAuditedBalance(TaxCalculationResponse result, Balance balance) {
        if (result != null &&
                (result.requestStatus?.isSuccess || result.requestStatus?.isPartialSuccess)) {
            balance.taxStatus = TaxStatus.AUDITED.name()
        }
        return balance
    }

    Balance updateBalance(TaxCalculationResponse result, Balance balance) {
        if (result != null &&
                (result.requestStatus?.isSuccess || result.requestStatus?.isPartialSuccess)) {
            Invoice resultInvoice = result.invoice[0]
            balance.taxAmount = resultInvoice.totalTaxAmount
            balance.taxIncluded = resultInvoice.calculationDirection?.equalsIgnoreCase('T')
            balance.balanceItems.eachWithIndex { BalanceItem item, int index ->

                resultInvoice.line.each { Line line ->
                    if (index + 1 == line.lineNumber) {
                        item.taxAmount = line.totalTaxAmount
                        Map<String, TaxItem> map = new HashMap<String, TaxItem>()
                        line.tax.each { Tax tax ->
                            String authority = getTaxAuthority(tax.authorityType)
                            def taxItem = map.get(authority)
                            if (taxItem == null) {
                                def newItem = new TaxItem()
                                newItem.taxAmount = BigDecimal.valueOf(tax.taxAmount.documentAmount)
                                newItem.taxRate = BigDecimal.valueOf(tax.taxRate)
                                newItem.taxAuthority = authority
                                newItem.isTaxExempt = tax.isExempt
                                map.put(authority, newItem)
                            }
                            else {
                                taxItem.taxAmount = taxItem.taxAmount.add(BigDecimal.valueOf(tax.taxAmount.documentAmount))
                                taxItem.taxRate = taxItem.taxRate.add(BigDecimal.valueOf(tax.taxRate))
                            }
                        }

                        Iterator iter = map.entrySet().iterator()
                        while (iter.hasNext()) {
                            Map.Entry entry = (Map.Entry) iter.next()
                            TaxItem savedItem = (TaxItem)entry.getValue()
                            item.addTaxItem(savedItem)
                        }
                    }
                }
            }
            balance.setTaxStatus(TaxStatus.TAXED.name())
        }
        else {
            balance.setTaxStatus(TaxStatus.FAILED.name())
        }
        return balance
    }

    String getTaxAuthority(String authorityType) {
        if (authorityType == null) {
            return null
        }
        TaxAuthority taxAuthority = AUTHORITY_MAP.get(authorityType.trim())
        return taxAuthority == null ? TaxAuthority.UNKNOWN.name() : taxAuthority.name()
    }

    SabrixAddress getSabrixAddress(Address address) {
        /*
        For US addresses: <COUNTRY> + <POSTCODE>
        For Non-US address: <COUNTRY>
         */
        def sabrixAddress = new SabrixAddress()
        sabrixAddress.country = address.countryId.value
        if (sabrixAddress.country in ['US', 'CA', 'GB']) {
            sabrixAddress.postcode = address.postalCode
        }
        return sabrixAddress
    }

    Promise<AddressValidationResponse> validateSabrixAddress(SabrixAddress address) {
        String validateAddressUrl = configuration.baseUrl + 'sabrix/addressvalidation'
        String content = xmlConvertor.getXml(address)
        def requestBuilder = buildRequest(validateAddressUrl, content)
        return requestBuilder.execute().recover { Throwable throwable ->
            LOGGER.error('Error_Build_Sabrix_Request.', throwable)
            throw AppErrors.INSTANCE.addressValidationError().exception()
        }.then { Response response ->
            AddressValidationResponse addressValidationResponse =
                    xmlConvertor.getAddressValidationResponse(response.responseBody)
            if (addressValidationResponse == null) {
                LOGGER.error('name=Error_Read_Sabrix_Response.')
                throw AppErrors.INSTANCE.addressValidationError().exception()
            }
            if (response.statusCode / 100 == 2) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug('name=Address_Validation_Response, response={}', addressValidationResponse.toString())
                }
                return Promise.pure(addressValidationResponse)
            }

            LOGGER.error('name=Error_Address_Validation.')
            LOGGER.info('name=Address_Validation_Response_Status_Code, statusCode={}', response.statusCode)
            List<ErrorDetail> details = new ArrayList<>();
            addressValidationResponse.message.each { ResponseMessage message ->
                LOGGER.info('name=Address_Validation_Response_Error_Message, message={}', message.details)
                details.add(new ErrorDetail(message.refersTo, message.details))
            }
            throw AppErrors.INSTANCE.addressValidationError(details.toArray(new ErrorDetail[0])).exception()
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
        if (validatedAddress.city != null) {
            address.city = validatedAddress.city.name
        }
        if (validatedAddress.postcode != null) {
            address.postalCode = validatedAddress.postcode.name
        }
        if (validatedAddress.state != null) {
            address.subCountry = validatedAddress.state.name
        }
        if (validatedAddress.province != null) {
            address.subCountry = validatedAddress.province.name
        }
        return address
    }

    Promise<RegistrationValidationResponse> validateVatId(RegistrationValidationRequest request) {
        String vatIdValidation = configuration.baseUrl + 'sabrix-extensions/registrationvalidation'
        String content = xmlConvertor.getXml(request)
        def requestBuilder = buildRequest(vatIdValidation, content)
        return requestBuilder.execute().recover { Throwable throwable ->
            LOGGER.error('Error_Build_Sabrix_Request.', throwable)
            return Promise.pure(null)
        }.then { Response response ->
            RegistrationValidationResponse vatValidationResponse =
                    xmlConvertor.getRegistrationValidationResponse(response.responseBody)
            if (vatValidationResponse == null) {
                LOGGER.error('name=Error_Read_Vat_Id_Validation_Response.')
                return Promise.pure(null)
            }
            if (response.statusCode / 100 == 2) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug('name=Vat_Id_Validation_Response, response={}', vatValidationResponse.toString())
                }
                return Promise.pure(vatValidationResponse)
            }
            LOGGER.info('name=Tax_Calculation_Response_Status_Code, statusCode={}', response.statusCode)
            return Promise.pure(null)
        }
    }
}
