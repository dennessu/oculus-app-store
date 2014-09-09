package com.junbo.store.rest.utils

import com.junbo.common.enumid.CountryId
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.PIType
import com.junbo.common.id.PaymentInstrumentId
import com.junbo.common.id.UserPersonalInfoId
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.common.model.Results
import com.junbo.identity.spec.v1.model.*
import com.junbo.identity.spec.v1.option.model.CountryGetOptions
import com.junbo.identity.spec.v1.option.model.UserPersonalInfoGetOptions
import com.junbo.langur.core.client.PathParamTranscoder
import com.junbo.langur.core.promise.Promise
import com.junbo.payment.spec.model.PageMetaData
import com.junbo.payment.spec.model.PaymentInstrument
import com.junbo.payment.spec.model.PaymentInstrumentSearchParam
import com.junbo.store.common.utils.CommonUtils
import com.junbo.store.spec.model.billing.Instrument
import com.junbo.store.spec.model.billing.InstrumentUpdateRequest
import com.junbo.store.spec.model.identity.PersonalInfo
import groovy.transform.CompileStatic
import org.apache.commons.lang3.ObjectUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.util.CollectionUtils
import org.springframework.util.StringUtils

import javax.annotation.Resource

/**
 * The InstrumentUtils class.
 */
@CompileStatic
@Component('storeInstrumentUtils')
class InstrumentUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(InstrumentUtils)

    @Resource(name = 'storeResourceContainer')
    private ResourceContainer resourceContainer

    @Resource(name = 'pathParamTranscoder')
    private PathParamTranscoder pathParamTranscoder

    @Resource(name = 'storeDataConverter')
    private DataConverter dataConvertor

    @Resource(name = 'storeIdentityUtils')
    private IdentityUtils identityUtils


    private static final int PAGE_SIZE = 100

    public Promise updateInstrument(User user, InstrumentUpdateRequest instrumentUpdateRequest) {
        if (instrumentUpdateRequest.instrument.self != null) {
            return getInstrument(user, instrumentUpdateRequest.instrument.self).then { Instrument old ->
                return innerUpdateInstrument(user, instrumentUpdateRequest.instrument, old)
            }
        } else {
            return innerCreateInstrument(user, instrumentUpdateRequest.instrument)
        }
    }

    private Promise innerUpdateInstrument(User user, Instrument instrument, Instrument oldInstrument) {

        boolean changed = false
        PaymentInstrument oldPaymentInstrument = oldInstrument.paymentInstrument

        if (oldInstrument.type != PIType.CREDITCARD.name()) {
            throw AppCommonErrors.INSTANCE.invalidOperation("Direct instrument update not allowed for instrument type ${instrument.type}.").exception()
        }

        Promise.pure(null).then {
            // update address
            if (ObjectUtils.equals(instrument.billingAddress, oldInstrument.billingAddress)) {
                return Promise.pure(null)
            }
            changed = true
            return resourceContainer.userPersonalInfoResource.create(
                    new UserPersonalInfo(userId: user.getId(), type: 'ADDRESS', value: ObjectMapperProvider.instance().valueToTree(dataConvertor.toIdentityAddress(instrument.billingAddress, new Address())))
            ).then { UserPersonalInfo info ->
                oldPaymentInstrument.billingAddressId = info.getId().value
                return Promise.pure(null)
            }
        }.then { // update phone number
            if (ObjectUtils.equals(instrument.phoneNumber, oldInstrument.phoneNumber)) {
                return Promise.pure(null)
            }
            changed = true
            return identityUtils.createPhoneInfoIfNotExist(user.getId(), new PersonalInfo(value: ObjectMapperProvider.instance().valueToTree(new PhoneNumber(info: instrument.phoneNumber)))).then { PersonalInfo info ->
                oldPaymentInstrument.phoneNumber = info.self.value
                return Promise.pure(null)
            }
        }.then {
            if (instrument.isDefault && user.defaultPI != instrument.self) {
                user.defaultPI = instrument.self
                return resourceContainer.userResource.put(user.getId(), user)
            }
            return Promise.pure(null)
        }.then { // update the pi
            if (changed) {
                return resourceContainer.paymentInstrumentResource.update(new PaymentInstrumentId(oldPaymentInstrument.getId()), oldPaymentInstrument)
            }
            return Promise.pure(null)
        }
    }

    private Promise innerCreateInstrument(User user, Instrument instrument) {
        PaymentInstrument paymentInstrument = new PaymentInstrument()
        Promise promise = Promise.pure(null)
        dataConvertor.toPaymentInstrument(instrument, paymentInstrument)

        if (instrument.type != PIType.CREDITCARD.name() && instrument.type != PIType.STOREDVALUE.name()) {
            throw AppCommonErrors.INSTANCE.invalidOperation("Direct instrument creation not allowed for instrument type ${instrument.type}.").exception()
        }

        // create address
        promise = promise.then {
            if (instrument.billingAddress != null) {
                return resourceContainer.userPersonalInfoResource.create(
                        new UserPersonalInfo(userId: user.getId(), type: 'ADDRESS', value: ObjectMapperProvider.instance().valueToTree(dataConvertor.toIdentityAddress(instrument.billingAddress, new Address())))
                ).syncThen { UserPersonalInfo info ->
                    paymentInstrument.billingAddressId = info.getId().value
                }
            } else {
                return Promise.pure(null)
            }
        }

        // create email
        promise = promise.then {
            UserPersonalInfoLink emailLink = user.emails?.find {UserPersonalInfoLink e -> return e.isDefault}
            if (emailLink == null) {
                emailLink = CollectionUtils.isEmpty(user.emails) ? null : user.emails[0]
            }
            paymentInstrument.email = emailLink.value.value
            return Promise.pure(null)
        }

        // create phone
        promise = promise.then {
            if (!StringUtils.isEmpty(instrument.phoneNumber)) {
                return identityUtils.createPhoneInfoIfNotExist(user.getId(), new PersonalInfo(value: ObjectMapperProvider.instance().valueToTree(new PhoneNumber(info: instrument.phoneNumber)))).then { PersonalInfo info ->
                    paymentInstrument.phoneNumber = info.self.value
                    return Promise.pure(null)
                }
            } else {
                return Promise.pure(null)
            }
        }

        return promise.then {
            paymentInstrument.userId = user.getId().value
            resourceContainer.paymentInstrumentResource.postPaymentInstrument(paymentInstrument)
        }.then { PaymentInstrument pi ->
            if (instrument.isDefault || (user.defaultPI == null)) {
                user.defaultPI = new PaymentInstrumentId(pi.getId())
                return resourceContainer.userResource.put(user.getId(), user)
            }
            return Promise.pure(null)
        }
    }

    public Promise<Instrument> findInstrumentByExample(User user, Instrument instrument, CountryId countryId) {
        List<Instrument> instruments
        getInstruments(user).then { List<Instrument> r ->
            instruments = r
            return Promise.pure(null)
        }.then {
            if (instrument.type == PIType.CREDITCARD.name()) {
                return Promise.pure(instruments.find {Instrument e -> e.accountNum == instrument.accountNum})
            } else if (instrument.type == PIType.STOREDVALUE.name()) {
                Country country
                resourceContainer.countryResource.get(countryId, new CountryGetOptions()).then { Country c ->
                    country = c
                    return Promise.pure(null)
                }.then {
                    return Promise.pure(instruments.find {Instrument e -> return e.storedValueCurrency == country.defaultCurrency.value && e.type == PIType.STOREDVALUE.name()})
                }
            }
            LOGGER.warn('name=StoreInstrument_NotCreditCard_Or_StoreValue, type={}', instrument.type)
            return Promise.pure(null)
        }
    }

    public Promise<List<Instrument>> getInstruments(User user) {
        PageMetaData pageMetaData = new PageMetaData(start: 0, count: PAGE_SIZE)
        List<PaymentInstrument> paymentInstruments = []
        List<Instrument> results = []
        CommonUtils.loop {
            resourceContainer.paymentInstrumentResource.searchPaymentInstrument(new PaymentInstrumentSearchParam(userId: user.getId()), pageMetaData).then { Results<PaymentInstrument> paymentInstrumentResults ->
                paymentInstruments.addAll(paymentInstrumentResults.items)
                pageMetaData.start += PAGE_SIZE
                return Promise.pure(paymentInstrumentResults.items.size() == PAGE_SIZE ? null : Promise.BREAK)
            }
        }.then {
            Promise.each(paymentInstruments) { PaymentInstrument pi ->
                Instrument storePi = new Instrument()
                dataConvertor.toInstrument(pi, storePi)
                storePi.isDefault = new PaymentInstrumentId(pi.getId()) == user.defaultPI
                return loadInstrumentPersonalInfo(storePi, pi).syncThen {
                    results << dataConvertor.toInstrument(pi, storePi)
                }
            }
        }.then {
            return Promise.pure(results)
        }
    }

    public Promise<Instrument> getInstrument(User user, PaymentInstrumentId instrumentId) {
        resourceContainer.paymentInstrumentResource.getById(instrumentId).then { PaymentInstrument pi ->
            Instrument storePi = new Instrument()
            dataConvertor.toInstrument(pi, storePi)
            return loadInstrumentPersonalInfo(storePi, pi).syncThen {
                storePi.isDefault = user.defaultPI == storePi.self
                return storePi
            }
        }
    }

    public Promise loadInstrumentPersonalInfo(Instrument instrument, PaymentInstrument paymentInstrument) {
        Promise promise = Promise.pure(null)

        promise = promise.then {
            if (paymentInstrument.billingAddressId != null) {
                return resourceContainer.userPersonalInfoResource.get(new UserPersonalInfoId(paymentInstrument.billingAddressId), new UserPersonalInfoGetOptions()).syncThen { UserPersonalInfo info ->
                    instrument.billingAddress = new com.junbo.store.spec.model.Address()
                    dataConvertor.toAddress(ObjectMapperProvider.instance().treeToValue(info.value, Address), instrument.billingAddress)
                }
            }
            return Promise.pure(null)
        }

        return promise.then {
            if (paymentInstrument.phoneNumber != null) {
                return resourceContainer.userPersonalInfoResource.get(new UserPersonalInfoId(paymentInstrument.phoneNumber), new UserPersonalInfoGetOptions()).syncThen { UserPersonalInfo info ->
                    instrument.phoneNumber = ObjectMapperProvider.instance().treeToValue(info.value, PhoneNumber).info
                }
            }
            return Promise.pure(null)
        }
    }
}
