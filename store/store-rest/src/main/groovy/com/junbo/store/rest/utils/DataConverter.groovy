package com.junbo.store.rest.utils

import com.junbo.common.enumid.CountryId
import com.junbo.common.enumid.LocaleId
import com.junbo.common.id.PaymentInstrumentId
import com.junbo.identity.spec.v1.model.PIType
import com.junbo.identity.spec.v1.model.UserName
import com.junbo.identity.spec.v1.model.UserPersonalInfo
import com.junbo.identity.spec.v1.model.UserPersonalInfoLink
import com.junbo.identity.spec.v1.model.UserTosAgreement
import com.junbo.identity.spec.v1.option.model.LocaleGetOptions
import com.junbo.payment.spec.model.PaymentInstrument
import com.junbo.payment.spec.model.TypeSpecificDetails
import com.junbo.store.clientproxy.ResourceContainer
import com.junbo.store.spec.model.Address
import com.junbo.store.spec.model.billing.Instrument
import com.junbo.store.spec.model.billing.PaymentOption
import com.junbo.store.spec.model.browse.document.Tos
import com.junbo.store.spec.model.identity.FullName
import com.junbo.store.spec.model.identity.PersonalInfo
import groovy.transform.CompileStatic
import org.apache.commons.lang3.StringUtils
import org.springframework.stereotype.Component

import javax.annotation.Resource

@CompileStatic
@Component('storeDataConverter')
class DataConverter {
    private static String DEFAULT_LOCALE = 'en_US'

    @Resource(name = 'storeResourceContainer')
    private ResourceContainer resourceContainer

    Instrument toInstrument(PaymentInstrument paymentInstrument, Instrument instrument) {
        instrument.self = paymentInstrument.getId() == null ? null : new PaymentInstrumentId(paymentInstrument.getId())
        instrument.type = com.junbo.common.id.PIType.get(paymentInstrument.type).name()
        instrument.accountName = paymentInstrument.accountName
        instrument.accountNum = paymentInstrument.accountNumber

        instrument.creditCardType = paymentInstrument.typeSpecificDetails?.creditCardType
        instrument.expireDate = paymentInstrument.typeSpecificDetails?.expireDate
        instrument.storedValueCurrency = paymentInstrument.typeSpecificDetails?.storedValueCurrency
        instrument.storedValueBalance = paymentInstrument.typeSpecificDetails?.storedValueBalance
        instrument.paymentInstrument = paymentInstrument
        return instrument
    }

    PaymentInstrument toPaymentInstrument(Instrument instrument, PaymentInstrument paymentInstrument) {
        paymentInstrument.setId(instrument.self?.value)
        paymentInstrument.type = com.junbo.common.id.PIType.valueOf(instrument.type).id
        paymentInstrument.accountName = instrument.accountName
        paymentInstrument.accountNumber = instrument.accountNum

        if (paymentInstrument.typeSpecificDetails == null) {
            paymentInstrument.typeSpecificDetails = new TypeSpecificDetails()
        }

        paymentInstrument.typeSpecificDetails.expireDate = instrument.expireDate
        paymentInstrument.typeSpecificDetails.storedValueCurrency = instrument.storedValueCurrency
        paymentInstrument.typeSpecificDetails.storedValueBalance = instrument.storedValueBalance
        return paymentInstrument
    }

    PaymentOption toPaymentOption(PIType piType, LocaleId locale) {
        PaymentOption paymentOption = new PaymentOption()
        paymentOption.type = piType.typeCode
        paymentOption.title = piType.locales.get(locale.value)?.get('description')?.asText()
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

    PersonalInfo toStorePersonalInfo(UserPersonalInfo userPersonalInfo, UserPersonalInfoLink link) {
        return new PersonalInfo(
                self: userPersonalInfo.getId(),
                value: userPersonalInfo.value,
                isDefault: link == null ? false : link.isDefault,
                type: userPersonalInfo.type
        )
    }

    Tos toStoreTos(com.junbo.identity.spec.v1.model.Tos tos, UserTosAgreement userTosAgreement, LocaleId localeId) {
        return new Tos(
            tosId: tos.getId(),
            type: tos.type,
            version: tos.version,
            title: getTitle(tos, localeId),
            content: tos.content,
            accepted: userTosAgreement != null,
            acceptedDate: userTosAgreement?.agreementTime
        )
    }

    FullName toFullName(UserName userName) {
        return new FullName(
                firstName: userName.givenName,
                middleName: userName.middleName,
                lastName: userName.familyName
        )
    }

    String getTitle(com.junbo.identity.spec.v1.model.Tos tos, LocaleId localeId) {
        if (tos == null || tos.locales == null || tos.locales.isEmpty()) {
            return null
        }

        Map<String, Boolean> circle = new HashMap<>()

        LocaleId current = localeId
        LocaleId fallback = null
        while (true) {
            com.junbo.identity.spec.v1.model.Locale locale = resourceContainer.localeResource.get(current, new LocaleGetOptions()).get()
            Boolean visited = circle.get(locale.getId().toString())
            if (visited) {
                break
            }
            circle.put(locale.getId().toString(), true)
            String title = tos.locales.get(locale.getId().toString())?.getTitle()?.toString()
            if (!StringUtils.isEmpty(title)) {
                return title
            }

            fallback = locale.fallbackLocale
            if (current == fallback || fallback == null) {
                break
            }
            current = fallback
        }

        return null
    }
}
