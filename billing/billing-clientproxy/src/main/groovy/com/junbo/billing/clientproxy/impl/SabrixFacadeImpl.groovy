/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.clientproxy.impl

import com.junbo.billing.spec.enums.BalanceType
import com.junbo.catalog.spec.enums.ItemType

import static com.ning.http.client.extra.ListenableFutureAdapter.asGuavaFuture

import com.junbo.billing.clientproxy.TaxFacade
import com.junbo.billing.clientproxy.impl.common.XmlConvertor
import com.junbo.billing.clientproxy.impl.sabrix.*
import com.junbo.billing.spec.enums.PropertyKey
import com.junbo.billing.spec.enums.TaxAuthority
import com.junbo.billing.spec.enums.TaxStatus
import com.junbo.billing.spec.error.AppErrors
import com.junbo.billing.spec.model.Balance
import com.junbo.billing.spec.model.BalanceItem
import com.junbo.billing.spec.model.TaxItem
import com.junbo.billing.spec.model.VatIdValidationResponse
import com.junbo.common.enumid.CountryId
import com.junbo.identity.spec.v1.model.Address
import com.junbo.langur.core.promise.Promise
import com.ning.http.client.AsyncHttpClient
import com.ning.http.client.AsyncHttpClient.BoundRequestBuilder
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
    @Resource(name = 'billingAsyncHttpClient')
    AsyncHttpClient asyncHttpClient

    @Resource(name = 'sabrixConfiguration')
    SabrixConfiguration configuration

    @Resource(name = 'xmlConvertor')
    XmlConvertor xmlConvertor

    static final int SUCCESSFUL_PROCESSING = 0
    static final String GOODS = 'GS'
    static final String ELECTRONIC_SERVICES = 'ES'
    static final String CALCULATION_DIRECTION_FORWARD = 'F'
    static final String CALCULATION_DIRECTION_REVERSE = 'R'
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
    private static final Map<String, String> PRODUCT_CODE_MAP
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

        Map<String, String> productCodeMap = new HashMap<String, String>()
        productCodeMap.put(ItemType.DIGITAL.name(), ProductCode.DOWNLOADABLE_SOFTWARE.code)
        productCodeMap.put(ItemType.APP.name(), ProductCode.DOWNLOADABLE_SOFTWARE.code)
        productCodeMap.put(ItemType.DOWNLOADED_ADDITION.name(), ProductCode.DOWNLOADABLE_SOFTWARE.code)
        productCodeMap.put(ItemType.VIRTUAL.name(), ProductCode.DIGITAL_CONTENT.code)
        productCodeMap.put(ItemType.IN_APP_CONSUMABLE.name(), ProductCode.DIGITAL_CONTENT.code)
        productCodeMap.put(ItemType.IN_APP_UNLOCK.name(), ProductCode.DIGITAL_CONTENT.code)
        productCodeMap.put(ItemType.PHYSICAL.name(), ProductCode.PHYSICAL_GOODS.code)
        productCodeMap.put(ItemType.STORED_VALUE.name(), ProductCode.STORE_BALANCE.code)

        PRODUCT_CODE_MAP = Collections.unmodifiableMap(productCodeMap)
    }
    @Override
    Promise<Balance> calculateTaxQuote(Balance balance, Address shippingAddress, Address piAddress) {
        Batch batch = generateBatch(balance, shippingAddress, piAddress, false)
        LOGGER.info('name=Tax_Calculation_Batch, batch={}', batch.toString())
        return calculateTax(batch).then { TaxCalculationResponse result ->
            return Promise.pure(updateBalance(result, balance))
        }
    }

    @Override
    Promise<Balance> calculateTax(Balance balance, Address shippingAddress, Address piAddress) {
        Batch batch = generateBatch(balance, shippingAddress, piAddress, true)
        LOGGER.info('name=Tax_Calculation_Quote_Batch, batch={}', batch.toString())
        return calculateTax(batch).then { TaxCalculationResponse result ->
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

    @Override
    Promise<VatIdValidationResponse> validateVatId(String vatId) {
        RegistrationValidationRequest request = generateRequest(vatId)
        LOGGER.info('name=Registration_Validation_Request, request={}', request.toString())
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
                throw AppErrors.INSTANCE.taxCalculationError('Do not support multi-order tax calculation now.').exception()
            }
            // combination of hostSystem, callingSystemNumber and uniqueInvoiceNumber makes a audit key
            invoice.hostSystem = configuration.hostSystem
            invoice.callingSystemNumber = configuration.callingSystemNumber
            invoice.uniqueInvoiceNumber = getUniqueInvoiceNumber(balance)
            invoice.invoiceNumber = balance.orderIds?.get(0)?.value
        }
        invoice.deliveryTerm = DeliveryTerm.DDP.name()
        invoice.companyRole = configuration.companyRole
        invoice.externalCompanyId = getEntity(piAddress).externalCompanyId
        boolean isRefund = BalanceType.REFUND.name() == balance.type
        if (isRefund) {
            invoice.originalInvoiceNumber = balance.orderIds?.get(0)?.value
            invoice.originalInvoiceDate = balance.propertySet.get(PropertyKey.ORIGINAL_INVOICE_DATE.name())
        }
        invoice.calculationDirection = isRefund ? CALCULATION_DIRECTION_REVERSE : CALCULATION_DIRECTION_FORWARD
        invoice.invoiceDate = DATE_FORMATTER.get().format(new Date())
        invoice.currencyCode = balance.currency
        invoice.isAudited = isAudited
        invoice.customerName = balance.propertySet.get(PropertyKey.CUSTOMER_NAME.name())
        invoice.customerNumber = balance.propertySet.get(PropertyKey.CUSTOMER_NUMBER.name())
        SabrixAddress billToAddress = toSabrixAddress(piAddress)
        SabrixAddress shipToAddress
        if (shippingAddress != null) {
            shipToAddress = toSabrixAddress(shippingAddress)
        }
        else {
            shipToAddress = billToAddress
        }
//        invoice.billTo = billToAddress
//        invoice.shipTo = shipToAddress
//        invoice.shipFrom = getSabrixShipFromAddress()
        def lines = generateLine(balance, billToAddress, shipToAddress)
        invoice.line = lines
        setupUserElement(invoice, balance)

        return invoice
    }

    List<Line> generateLine(Balance balance, SabrixAddress billToAddress, SabrixAddress shipToAddress) {
        def lines = []
        balance.balanceItems.eachWithIndex { BalanceItem item, int index ->
            Line line = new Line()
            line.id = index + 1
            line.lineNumber = line.id
            line.grossAmount = item.amount?.toDouble()
            line.taxAmount = item.taxAmount?.toDouble()
            line.discountAmount = item.discountAmount?.toDouble()
            line.productCode = item.financeId
            line.transactionType = getTransactionType(item)
            line.productCode = getProductCode(item)
            line.description = item.propertySet.get(PropertyKey.ITEM_DESCRIPTION.name())
            line.partNumber = item.propertySet.get(PropertyKey.ORDER_ITEM_ID.name())
            if (item.propertySet.get(PropertyKey.VAT_ID.name()) != null) {
                def registrations = new Registrations()
                registrations.buyerRole = item.propertySet.get(PropertyKey.VAT_ID.name())
                line.registrations = registrations
            }
            line.vendorNumber = item.propertySet.get(PropertyKey.VENDOR_NUMBER.name())
            line.vendorName = item.propertySet.get(PropertyKey.VENDOR_NAME.name())
            line.billTo = billToAddress
            line.shipTo = shipToAddress
            if (line.transactionType == GOODS) {
                line.shipFrom = getSabrixShipFromAddress()
            }
            setupUserElement(line, item)
            lines << line
        }

        return lines
    }

    String getUniqueInvoiceNumber(Balance balance) {
        return balance.orderIds[0]?.value
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
        if (balance.propertySet.get(PropertyKey.TAX_STATUS.name()) != null) {
            UserElement attribute3 = new UserElement()
            attribute3.name = 'ATTRIBUTE3'
            attribute3.value = balance.propertySet.get(PropertyKey.TAX_STATUS.name())
            invoice.userElement.add(attribute3)
        }
        if (balance.propertySet.get(PropertyKey.PAYMENT_METHOD.name()) != null) {
            UserElement attribute4 = new UserElement()
            attribute4.name = 'ATTRIBUTE4'
            attribute4.value = balance.propertySet.get(PropertyKey.PAYMENT_METHOD.name())
            invoice.userElement.add(attribute4)
        }
        if (balance.propertySet.get(PropertyKey.EXCHANGE_RATE.name()) != null) {
            UserElement attribute5 = new UserElement()
            attribute5.name = 'ATTRIBUTE5'
            attribute5.value = balance.propertySet.get(PropertyKey.EXCHANGE_RATE.name())
            invoice.userElement.add(attribute5)
        }
        if (balance.propertySet.get(PropertyKey.IP_ADDRESS.name()) != null) {
            UserElement attribute6 = new UserElement()
            attribute6.name = 'ATTRIBUTE6'
            attribute6.value = balance.propertySet.get(PropertyKey.IP_ADDRESS.name())
            invoice.userElement.add(attribute6)
        }
        if (balance.propertySet.get(PropertyKey.FUNCTIONAL_CURRENCY.name()) != null) {
            UserElement attribute7 = new UserElement()
            attribute7.name = 'ATTRIBUTE7'
            attribute7.value = balance.propertySet.get(PropertyKey.FUNCTIONAL_CURRENCY.name())
            invoice.userElement.add(attribute7)
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
            case 'PHYSICAL':
                return GOODS
            default:
                return ELECTRONIC_SERVICES
        }
    }

    String getProductCode(BalanceItem item) {
        String type = item.propertySet.get(PropertyKey.ITEM_TYPE.name())
        if (type == null) {
            return null
        }

        return PRODUCT_CODE_MAP.get(type)
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
        // TODO: update ship from address to entity/warehouse
        SabrixAddress sabrixAddress = new SabrixAddress()
        sabrixAddress.country = configuration.shipFromCountry
        return sabrixAddress
    }

    Promise<TaxCalculationResponse> calculateTax(Batch batch) {
        String taxCalculationUrl = configuration.baseUrl + 'sabrix/xmlinvoice'
        String content = xmlConvertor.getXml(batch)
        def requestBuilder = buildRequest(taxCalculationUrl, content)
        return Promise.wrap(asGuavaFuture(requestBuilder.execute())).recover { Throwable throwable ->
            LOGGER.error('Error_Build_Sabrix_Request.', throwable)
            return Promise.pure(null)
        }.then { Response response ->
            TaxCalculationResponse result = xmlConvertor.getTaxCalculationResponse(response.responseBody)
            if (result == null) {
                LOGGER.error('name=Error_Read_Sabrix_Tax_Calculation_Response.')
                return Promise.pure(null)
            }
            if (response.statusCode / 100 == 2) {
                LOGGER.info('name=Tax_Calculation_Response, response={}', result.toString())
                return Promise.pure(result)
            }
            LOGGER.error('name=Error_Tax_Calculation, description={}', result.requestStatus?.error?.description)
            LOGGER.info('name=Tax_Calculation_Response_Status_Code, statusCode={}', response.statusCode)
            return Promise.pure(null)
        }
    }

    Balance updateBalance(TaxCalculationResponse result, Balance  balance) {
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
        if (sabrixAddress.country == 'US') {
            sabrixAddress.postcode = address.postalCode
        }
        return sabrixAddress
    }

    Promise<AddressValidationResponse> validateSabrixAddress(SabrixAddress address) {
        String validateAddressUrl = configuration.baseUrl + 'sabrix/addressvalidation'
        String content = xmlConvertor.getXml(address)
        def requestBuilder = buildRequest(validateAddressUrl, content)
        return Promise.wrap(asGuavaFuture(requestBuilder.execute())).recover { Throwable throwable ->
            LOGGER.error('Error_Build_Sabrix_Request.', throwable)
            throw AppErrors.INSTANCE.addressValidationError('Fail to build request.').exception()
        }.then { Response response ->
            AddressValidationResponse addressValidationResponse =
                    xmlConvertor.getAddressValidationResponse(response.responseBody)
            if (addressValidationResponse == null) {
                LOGGER.error('name=Error_Read_Sabrix_Response.')
                throw AppErrors.INSTANCE.addressValidationError('Fail to read response.').exception()
            }
            if (response.statusCode / 100 == 2) {
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

    Promise<RegistrationValidationResponse> validateVatId(RegistrationValidationRequest request) {
        String vatIdValidation = configuration.baseUrl + 'sabrix-extensions/registrationvalidation'
        String content = xmlConvertor.getXml(request)
        def requestBuilder = buildRequest(vatIdValidation, content)
        return Promise.wrap(asGuavaFuture(requestBuilder.execute())).recover { Throwable throwable ->
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
                LOGGER.info('name=Vat_Id_Validation_Response, response={}', vatValidationResponse.toString())
                return Promise.pure(vatValidationResponse)
            }
            LOGGER.info('name=Tax_Calculation_Response_Status_Code, statusCode={}', response.statusCode)
            return Promise.pure(null)
        }
    }
}
