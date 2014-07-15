package com.junbo.store.rest.utils
import com.junbo.common.id.PaymentInstrumentId
import com.junbo.common.id.UserId
import com.junbo.common.id.UserPersonalInfoId
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.common.util.IdFormatter
import com.junbo.identity.spec.v1.model.Address
import com.junbo.identity.spec.v1.model.Email
import com.junbo.identity.spec.v1.model.PhoneNumber
import com.junbo.identity.spec.v1.model.UserPersonalInfo
import com.junbo.identity.spec.v1.option.model.UserPersonalInfoGetOptions
import com.junbo.langur.core.client.PathParamTranscoder
import com.junbo.langur.core.promise.Promise
import com.junbo.payment.spec.model.PaymentInstrument
import com.junbo.store.spec.model.billing.BillingProfileUpdateRequest
import com.junbo.store.spec.model.billing.Instrument
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

    Promise addInstrument(BillingProfileUpdateRequest billingProfileUpdateRequest, UserId userId) {
        Instrument instrument = billingProfileUpdateRequest.instrument
        PaymentInstrument paymentInstrument = new PaymentInstrument()
        UserPersonalInfo email = null, address = null, phone = null
        Promise promise = Promise.pure(null)

        // create address
        promise = promise.then {
            if (instrument.billingAddress != null) {
                com.junbo.identity.spec.v1.model.Address idAddress = new com.junbo.identity.spec.v1.model.Address()
                dataConvertor.toIdentityAddress(instrument.billingAddress, idAddress)
                return resourceContainer.userPersonalInfoResource.create(
                        new UserPersonalInfo(
                                userId: userId,
                                type: 'ADDRESS',
                                value: ObjectMapperProvider.instance().valueToTree(idAddress)
                        )
                ).syncThen { UserPersonalInfo info ->
                    address = info
                }
            } else {
                return Promise.pure(null)
            }
        }

        // create email
        promise = promise.then {
            if (!StringUtils.isEmpty(instrument.email)) {
                return resourceContainer.userPersonalInfoResource.create(
                        new UserPersonalInfo(
                                userId: userId,
                                type: 'EMAIL',
                                value: ObjectMapperProvider.instance().valueToTree(new Email(info: instrument.email))
                        )
                ).syncThen { UserPersonalInfo info ->
                    email = info
                }
            } else {
                return Promise.pure(null)
            }
        }

        // create phone
        promise = promise.then {
            if (!StringUtils.isEmpty(instrument.phoneNumber)) {
                return resourceContainer.userPersonalInfoResource.create(
                        new UserPersonalInfo(
                                userId: userId,
                                type: 'PHONE',
                                value: ObjectMapperProvider.instance().valueToTree(new PhoneNumber(info: instrument.phoneNumber))
                        )
                ).syncThen { UserPersonalInfo info ->
                    phone = info
                }
            } else {
                return Promise.pure(null)
            }
        }

        return promise.then {
            dataConvertor.toPaymentInstrument(billingProfileUpdateRequest.instrument, paymentInstrument)
            paymentInstrument.userId = userId.value
            paymentInstrument.email = email?.getId()?.value
            paymentInstrument.billingAddressId = address?.getId()?.value
            paymentInstrument.phoneNumber = phone?.getId()?.value
            resourceContainer.paymentInstrumentResource.postPaymentInstrument(paymentInstrument)
        }
    }

    public Promise updateInstrument(BillingProfileUpdateRequest billingProfileUpdateRequest, UserId userId) {
        getInstrument(billingProfileUpdateRequest.instrument.instrumentId).then { Instrument existInstrument ->
            Instrument updateInstrument = billingProfileUpdateRequest.instrument
            PaymentInstrument paymentInstrument = new PaymentInstrument()
            dataConvertor.toPaymentInstrument(billingProfileUpdateRequest.instrument, paymentInstrument)

            // update address
            Promise.pure(null).then { // update address
                if (ObjectUtils.equals(existInstrument.billingAddress, updateInstrument.billingAddress)) {
                    paymentInstrument.billingAddressId = existInstrument.paymentInstrument.billingAddressId
                    return Promise.pure(null)
                } else if (updateInstrument.billingAddress == null) {
                    return Promise.pure(null)
                }
                com.junbo.identity.spec.v1.model.Address idAddress = new com.junbo.identity.spec.v1.model.Address()
                dataConvertor.toIdentityAddress(updateInstrument.billingAddress, idAddress)
                return resourceContainer.userPersonalInfoResource.create(
                        new UserPersonalInfo(
                                userId: userId,
                                type: 'ADDRESS',
                                value: ObjectMapperProvider.instance().valueToTree(idAddress)
                        )
                ).then { UserPersonalInfo info ->
                    paymentInstrument.billingAddressId = info.getId().value
                    return Promise.pure(null)
                }
            }.then { // update email
                if (ObjectUtils.equals(existInstrument.email, updateInstrument.email)) {
                    paymentInstrument.email = existInstrument.paymentInstrument.email
                    return Promise.pure(null)
                } else if (updateInstrument.email == null) {
                    return Promise.pure(null)
                }
                return resourceContainer.userPersonalInfoResource.create(
                        new UserPersonalInfo(
                                userId: userId,
                                type: 'EMAIL',
                                value: ObjectMapperProvider.instance().valueToTree(new Email(info: updateInstrument.email))
                        )
                ).then { UserPersonalInfo info ->
                    paymentInstrument.email = info.getId().value
                    return Promise.pure(null)
                }

            }.then { // update phone number
                if (ObjectUtils.equals(existInstrument.phoneNumber, updateInstrument.phoneNumber)) {
                    paymentInstrument.phoneNumber = existInstrument.paymentInstrument.phoneNumber
                    return Promise.pure(null)
                } else if (updateInstrument.phoneNumber == null) {
                    return Promise.pure(null)
                }

                return resourceContainer.userPersonalInfoResource.create(
                        new UserPersonalInfo(
                                userId: userId,
                                type: 'PHONE',
                                value: ObjectMapperProvider.instance().valueToTree(new PhoneNumber(info: updateInstrument.phoneNumber))
                        )
                ).then { UserPersonalInfo info ->
                    paymentInstrument.phoneNumber = info.getId().value
                    return Promise.pure(null)
                }

            }.then { // update the pi
                resourceContainer.paymentInstrumentResource.update(new PaymentInstrumentId(paymentInstrument.getId()), paymentInstrument)
            }
        }
    }

    public Promise removeInstrument(BillingProfileUpdateRequest billingProfileUpdateRequest) {
        resourceContainer.paymentInstrumentResource.getById(new PaymentInstrumentId(IdFormatter.decodeId(PaymentInstrumentId, billingProfileUpdateRequest.instrument.instrumentId))).then { PaymentInstrument paymentInstrument ->
            resourceContainer.paymentInstrumentResource.delete(new PaymentInstrumentId(IdFormatter.decodeId(PaymentInstrumentId, billingProfileUpdateRequest.instrument.instrumentId)))
        }
    }

    public Promise<Instrument> getInstrument(String instrumentId) {
        resourceContainer.paymentInstrumentResource.getById(new PaymentInstrumentId(IdFormatter.decodeId(PaymentInstrumentId, instrumentId))).then { PaymentInstrument pi ->
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
                    instrument.email = ObjectMapperProvider.instance().treeToValue(info.getValue(), Email).info
                }
            }
            return Promise.pure(null)
        }

        promise = promise.then {
            if (paymentInstrument.billingAddressId != null) {
                return resourceContainer.userPersonalInfoResource.get(new UserPersonalInfoId(paymentInstrument.billingAddressId), new UserPersonalInfoGetOptions()).syncThen { UserPersonalInfo info ->
                    instrument.billingAddress = new com.junbo.store.spec.model.Address()
                    dataConvertor.toAddress(ObjectMapperProvider.instance().treeToValue(info.getValue(), Address), instrument.billingAddress)
                }
            }
            return Promise.pure(null)
        }

        return promise.then {
            if (paymentInstrument.phoneNumber != null) {
                return resourceContainer.userPersonalInfoResource.get(new UserPersonalInfoId(paymentInstrument.phoneNumber), new UserPersonalInfoGetOptions()).syncThen { UserPersonalInfo info ->
                    instrument.phoneNumber = ObjectMapperProvider.instance().treeToValue(info.getValue(), PhoneNumber).info
                }
            }
            return Promise.pure(null)
        }
    }
}
