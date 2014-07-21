package com.junbo.store.rest.utils
import com.junbo.common.id.PaymentInstrumentId
import com.junbo.common.id.UserId
import com.junbo.common.id.UserPersonalInfoId
import com.junbo.identity.spec.v1.model.UserPersonalInfo
import com.junbo.identity.spec.v1.option.model.UserPersonalInfoGetOptions
import com.junbo.langur.core.client.PathParamTranscoder
import com.junbo.langur.core.promise.Promise
import com.junbo.payment.spec.model.PaymentInstrument
import com.junbo.store.spec.model.billing.BillingProfileUpdateRequest
import com.junbo.store.spec.model.billing.Instrument
import com.junbo.store.spec.model.identity.PersonalInfo
import groovy.transform.CompileStatic
import org.apache.commons.lang3.ObjectUtils
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils

import javax.annotation.Resource
/**
 * The InstrumentUtils class.
 */
@CompileStatic
@Component('storeInstrumentUtils')
class InstrumentUtils {

    @Resource(name = 'storeResourceContainer')
    private ResourceContainer resourceContainer

    @Resource(name = 'pathParamTranscoder')
    private PathParamTranscoder pathParamTranscoder

    @Resource(name = 'storeDataConverter')
    private DataConverter dataConvertor

    @Resource(name = 'storeIdentityUtils')
    private IdentityUtils identityUtils

    Promise addInstrument(BillingProfileUpdateRequest billingProfileUpdateRequest, UserId userId) {
        Instrument instrument = billingProfileUpdateRequest.instrument
        PaymentInstrument paymentInstrument = new PaymentInstrument()
        PersonalInfo email = null, address = null, phone = null
        Promise promise = Promise.pure(null)

        // create address
        promise = promise.then {
            if (instrument.billingAddress?.value != null) {
                return resourceContainer.userPersonalInfoResource.create(
                        new UserPersonalInfo(userId: userId, type: 'ADDRESS', value: instrument.billingAddress.value)
                ).syncThen { UserPersonalInfo info ->
                    address = dataConvertor.toStorePersonalInfo(info, null)
                }
            } else {
                return Promise.pure(null)
            }
        }

        // create email
        promise = promise.then {
            if (!StringUtils.isEmpty(instrument.email?.value)) {
                return identityUtils.createEmailInfoIfNotExist(userId, instrument.email).syncThen { PersonalInfo info ->
                    email = info
                }
            } else {
                return Promise.pure(null)
            }
        }

        // create phone
        promise = promise.then {
            if (!StringUtils.isEmpty(instrument.phoneNumber?.value)) {
                return identityUtils.createPhoneInfoIfNotExist(userId, instrument.phoneNumber).syncThen { PersonalInfo info ->
                    phone = info
                }
            } else {
                return Promise.pure(null)
            }
        }

        return promise.then {
            dataConvertor.toPaymentInstrument(billingProfileUpdateRequest.instrument, paymentInstrument)
            paymentInstrument.userId = userId.value
            paymentInstrument.email = email?.userPersonalInfoId?.value
            paymentInstrument.billingAddressId = address?.userPersonalInfoId?.value
            paymentInstrument.phoneNumber = phone?.userPersonalInfoId?.value
            resourceContainer.paymentInstrumentResource.postPaymentInstrument(paymentInstrument)
        }
    }

    public Promise updateInstrument(BillingProfileUpdateRequest billingProfileUpdateRequest, UserId userId) {
        getInstrument(billingProfileUpdateRequest.instrument.instrumentId).then { Instrument existInstrument ->
            Instrument updateInstrument = billingProfileUpdateRequest.instrument
            PaymentInstrument paymentInstrument = new PaymentInstrument()
            dataConvertor.toPaymentInstrument(existInstrument, paymentInstrument)
            paymentInstrument.userId = userId.value
            paymentInstrument.setId(existInstrument.paymentInstrument.getId())

            // update address
            Promise.pure(null).then { // update address
                if (ObjectUtils.equals(existInstrument.billingAddress?.value, updateInstrument.billingAddress?.value)) {
                    paymentInstrument.billingAddressId = existInstrument.paymentInstrument.billingAddressId
                    return Promise.pure(null)
                } else if (updateInstrument.billingAddress?.value == null) {
                    return Promise.pure(null)
                }
                return resourceContainer.userPersonalInfoResource.create(
                        new UserPersonalInfo(userId: userId, type: 'ADDRESS', value:  updateInstrument.billingAddress.value)
                ).then { UserPersonalInfo info ->
                    paymentInstrument.billingAddressId = info.getId().value
                    return Promise.pure(null)
                }


            }.then { // update email
                if (ObjectUtils.equals(existInstrument.email?.value, updateInstrument.email?.value)) {
                    paymentInstrument.email = existInstrument.paymentInstrument.email
                    return Promise.pure(null)
                } else if (updateInstrument.email?.value == null) {
                    return Promise.pure(null)
                }
                return identityUtils.createEmailInfoIfNotExist(userId, updateInstrument.email).then { PersonalInfo info ->
                    paymentInstrument.email = info.userPersonalInfoId.value
                    return Promise.pure(null)
                }


            }.then { // update phone number
                if (ObjectUtils.equals(existInstrument.phoneNumber?.value, updateInstrument.phoneNumber)) {
                    paymentInstrument.phoneNumber = existInstrument.paymentInstrument.phoneNumber
                    return Promise.pure(null)
                } else if (updateInstrument.phoneNumber == null) {
                    return Promise.pure(null)
                }
                return identityUtils.createPhoneInfoIfNotExist(userId, updateInstrument.phoneNumber).then { PersonalInfo info ->
                    paymentInstrument.phoneNumber = info.userPersonalInfoId.value
                    return Promise.pure(null)
                }


            }.then { // update the pi
                resourceContainer.paymentInstrumentResource.update(new PaymentInstrumentId(paymentInstrument.getId()), paymentInstrument)
            }
        }
    }

    public Promise removeInstrument(BillingProfileUpdateRequest billingProfileUpdateRequest) {
        resourceContainer.paymentInstrumentResource.delete( billingProfileUpdateRequest.instrument.instrumentId)
    }

    public Promise<Instrument> getInstrument(PaymentInstrumentId instrumentId) {
        resourceContainer.paymentInstrumentResource.getById(instrumentId).then { PaymentInstrument pi ->
            Instrument storePi = new Instrument()
            dataConvertor.toInstrument(pi, storePi)
            return loadInstrumentPersonalInfo(storePi, pi).syncThen {
                return storePi
            }
        }
    }

    public Promise loadInstrumentPersonalInfo(Instrument instrument, PaymentInstrument paymentInstrument) {
        Promise promise = Promise.pure(null)

        promise = promise.then {
            if (paymentInstrument.email != null) {
                return resourceContainer.userPersonalInfoResource.get(new UserPersonalInfoId(paymentInstrument.email), new UserPersonalInfoGetOptions()).syncThen { UserPersonalInfo info ->
                    instrument.email = dataConvertor.toStorePersonalInfo(info, null)
                }
            }
            return Promise.pure(null)
        }

        promise = promise.then {
            if (paymentInstrument.billingAddressId != null) {
                return resourceContainer.userPersonalInfoResource.get(new UserPersonalInfoId(paymentInstrument.billingAddressId), new UserPersonalInfoGetOptions()).syncThen { UserPersonalInfo info ->
                    instrument.billingAddress = dataConvertor.toStorePersonalInfo(info, null)
                }
            }
            return Promise.pure(null)
        }

        return promise.then {
            if (paymentInstrument.phoneNumber != null) {
                return resourceContainer.userPersonalInfoResource.get(new UserPersonalInfoId(paymentInstrument.phoneNumber), new UserPersonalInfoGetOptions()).syncThen { UserPersonalInfo info ->
                    instrument.phoneNumber = dataConvertor.toStorePersonalInfo(info, null)
                }
            }
            return Promise.pure(null)
        }
    }
}
