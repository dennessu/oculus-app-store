package com.junbo.store.rest.utils

import com.junbo.common.enumid.CountryId
import com.junbo.common.id.PaymentInstrumentId
import com.junbo.common.util.IdFormatter
import com.junbo.identity.spec.v1.model.PIType
import com.junbo.payment.spec.model.PaymentInstrument
import com.junbo.payment.spec.model.TypeSpecificDetails
import com.junbo.store.spec.model.Address
import com.junbo.store.spec.model.billing.Instrument
import com.junbo.store.spec.model.billing.PaymentOption
import groovy.transform.CompileStatic
import org.springframework.stereotype.Component

import javax.annotation.Resource

@CompileStatic
@Component('storeDataConverter')
class DataConverter {

    @Resource(name = 'storeResourceContainer')
    private ResourceContainer resourceContainer

    Instrument toInstrument(PaymentInstrument paymentInstrument, Instrument instrument) {
        if (paymentInstrument.getId() != null) {
            instrument.instrumentId = IdFormatter.encodeId(new PaymentInstrumentId(paymentInstrument.getId()))
        }

        instrument.type = com.junbo.common.id.PIType.get(paymentInstrument.type).name()
        instrument.accountName = paymentInstrument.accountName
        instrument.accountNum = paymentInstrument.accountNum

        instrument.creditCardType = paymentInstrument.typeSpecificDetails?.creditCardType
        instrument.expireDate = paymentInstrument.typeSpecificDetails?.expireDate
        instrument.encryptedCvmCode = paymentInstrument.typeSpecificDetails?.encryptedCvmCode

        instrument.storedValueCurrency = paymentInstrument.typeSpecificDetails?.storedValueCurrency
        instrument.storedValueBalance = paymentInstrument.typeSpecificDetails?.storedValueBalance
        instrument.paymentInstrument = paymentInstrument
        return instrument
    }

    PaymentInstrument toPaymentInstrument(Instrument instrument, PaymentInstrument paymentInstrument) {
        if (instrument.instrumentId != null) {
            paymentInstrument.id = IdFormatter.decodeId(PaymentInstrumentId, instrument.instrumentId)
        }

        paymentInstrument.type = com.junbo.common.id.PIType.valueOf(instrument.type).id
        paymentInstrument.accountName = instrument.accountName
        paymentInstrument.accountNum = instrument.accountNum

        if (paymentInstrument.typeSpecificDetails == null) {
            paymentInstrument.typeSpecificDetails = new TypeSpecificDetails()
        }

        paymentInstrument.typeSpecificDetails.expireDate = instrument.expireDate
        paymentInstrument.typeSpecificDetails.encryptedCvmCode = instrument.encryptedCvmCode
        paymentInstrument.typeSpecificDetails.storedValueCurrency = instrument.storedValueCurrency
        paymentInstrument.typeSpecificDetails.storedValueBalance = instrument.storedValueBalance
        return paymentInstrument
    }

    PaymentOption toPaymentOption(PIType piType, String locale) {
        PaymentOption paymentOption = new PaymentOption()
        paymentOption.type = piType.typeCode
        paymentOption.title = piType.locales.get(locale)?.get('description')?.asText()
        return paymentOption
    }

    Address toAddress(com.junbo.identity.spec.v1.model.Address identityAddress, Address address) {
        address.city = identityAddress.city
        address.street1 = identityAddress.street1
        address.street2 = identityAddress.street2
        address.street3 = identityAddress.street3
        address.city = identityAddress.city
        address.subCountry = identityAddress.subCountry
        address.country = identityAddress.countryId.value
        address.subCountry = identityAddress.subCountry
        address.postalCode = identityAddress.postalCode
        return address
    }

    com.junbo.identity.spec.v1.model.Address toIdentityAddress(Address address, com.junbo.identity.spec.v1.model.Address identityAddress) {
        identityAddress.city = address.city
        identityAddress.street1 = address.street1
        identityAddress.street2 = address.street2
        identityAddress.street3 = address.street3
        identityAddress.city = address.city
        identityAddress.subCountry = address.subCountry
        identityAddress.countryId = new CountryId(address.country)
        identityAddress.subCountry = address.subCountry
        identityAddress.postalCode = address.postalCode
        return identityAddress
    }

}