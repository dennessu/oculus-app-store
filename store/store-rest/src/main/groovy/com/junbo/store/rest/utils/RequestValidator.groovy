package com.junbo.store.rest.utils

import com.junbo.common.enumid.CountryId
import com.junbo.common.enumid.LocaleId
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.OfferId
import com.junbo.common.id.OrderId
import com.junbo.common.id.PIType
import com.junbo.common.id.UserId
import com.junbo.common.util.IdFormatter
import com.junbo.identity.spec.v1.model.Country
import com.junbo.identity.spec.v1.option.model.CountryGetOptions
import com.junbo.identity.spec.v1.option.model.LocaleGetOptions
import com.junbo.langur.core.promise.Promise
import com.junbo.store.clientproxy.FacadeContainer
import com.junbo.store.spec.error.AppErrors
import com.junbo.store.spec.model.Challenge
import com.junbo.store.spec.model.ChallengeAnswer
import com.junbo.store.spec.model.billing.BillingProfileGetRequest
import com.junbo.store.spec.model.billing.InstrumentUpdateRequest
import com.junbo.store.spec.model.identity.UserProfileUpdateRequest
import com.junbo.store.spec.model.identity.UserProfileUpdateResponse
import com.junbo.store.spec.model.login.*
import com.junbo.store.spec.model.purchase.CommitPurchaseRequest
import com.junbo.store.spec.model.purchase.MakeFreePurchaseRequest
import com.junbo.store.spec.model.purchase.PreparePurchaseRequest
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils

import javax.annotation.Resource

/**
 * The RequestValidator class.
 */
@CompileStatic
@Component('storeRequestValidator')
class RequestValidator {

    @Value('${store.login.requireDetailsForCreate}')
    private boolean requireDetailsForCreate

    @Resource(name = 'storeResourceContainer')
    ResourceContainer resourceContainer

    @Resource(name = 'storeFacadeContainer')
    private FacadeContainer facadeContainer

    void validateUserNameCheckRequest(UserNameCheckRequest request) {
        if (request == null) {
            throw AppCommonErrors.INSTANCE.requestBodyRequired().exception()
        }

        if (StringUtils.isEmpty(request.email) && StringUtils.isEmpty(request.username)) {
            throw AppCommonErrors.INSTANCE.fieldRequired('email or username').exception()
        }

        if (!StringUtils.isEmpty(request.email) && !StringUtils.isEmpty(request.username)) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('email or username', 'only one of the fields [email, username] could be specified at a time').exception()
        }
    }

    void validateUserCredentialRateRequest(UserCredentialRateRequest request) {
        if (request == null) {
            throw AppCommonErrors.INSTANCE.requestBodyRequired().exception()
        }

        if (request.userCredential == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('userCredential').exception()
        }

        notEmpty(request.userCredential.type, 'userCredential.type')
        notEmpty(request.userCredential.value, 'userCredential.value')
    }

    void validateCreateUserRequest(CreateUserRequest request) {
        if (request == null) {
            throw AppCommonErrors.INSTANCE.requestBodyRequired().exception()
        }

        notEmpty(request.username, 'username')
        notEmpty(request.email, 'email')
        notEmpty(request.password, 'password')
        notEmpty(request.cor, 'cor')
        notEmpty(request.preferredLocale, 'preferredLocale')

        if (requireDetailsForCreate) {
            notEmpty(request.pin, 'pin')
            notEmpty(request.dob, 'dob')
            notEmpty(request.firstName, 'firstName')
            notEmpty(request.lastName, 'lastName')
        }
    }

    void validateUserSignInRequest(UserSignInRequest request) {
        if (request == null) {
            throw AppCommonErrors.INSTANCE.requestBodyRequired().exception()
        }

        notEmpty(request.username, 'username')
        notEmpty(request.userCredential, 'userCredential')
        notEmpty(request.userCredential.type, 'userCredential.type')
        notEmpty(request.userCredential.value, 'userCredential.value')

        if (!'PASSWORD'.equalsIgnoreCase(request.userCredential.type)) { //
            throw AppCommonErrors.INSTANCE.fieldInvalid('userCredential.type', 'type must be PASSWORD ').exception()
        }
    }

    void validateAuthTokenRequest(AuthTokenRequest request) {
        if (request == null) {
            throw AppCommonErrors.INSTANCE.requestBodyRequired().exception()
        }

        notEmpty(request.refreshToken, 'refreshToken')
    }

    private Challenge ensurePasswordChallenge(UserId userId, ChallengeAnswer challengeAnswer) {
        if (challengeAnswer == null) {
            return new Challenge(type: 'PASSWORD')
        }

        if (challengeAnswer.type != 'PASSWORD' || org.apache.commons.lang3.StringUtils.isEmpty(challengeAnswer.password)) {
            return new Challenge(type: 'PASSWORD')
        }

        return null
    }

    UserProfileUpdateResponse validateUserProfileUpdateRequest(UserId userId, UserProfileUpdateRequest request) {
        if (request == null) {
            throw AppCommonErrors.INSTANCE.requestBodyRequired().exception()
        }

        notEmpty(request.userProfile, 'userProfile')

        if (!StringUtils.isEmpty(request.userProfile.password)
                || !StringUtils.isEmpty(request.userProfile.pin)) {

            Challenge challenge = ensurePasswordChallenge(userId, request.challengeAnswer)
            if (challenge != null) {
                return new UserProfileUpdateResponse(challenge: challenge)
            }
        }

        return null
    }

    Promise validateBillingProfileGetRequest(BillingProfileGetRequest request) {
        notEmpty(request.country, 'country')
        return Promise.pure(null)
    }

    Promise<Country> validateAndGetCountry(CountryId country) {
        resourceContainer.countryResource.get(country, new CountryGetOptions())
    }

    Promise<com.junbo.identity.spec.v1.model.Locale> validateAndGetLocale(Country country, LocaleId locale) {
        if (locale != null) {
            return resourceContainer.localeResource.get(locale, new LocaleGetOptions())
        }
        return resourceContainer.localeResource.get(country.defaultLocale, new LocaleGetOptions())
    }

    Promise<com.junbo.identity.spec.v1.model.Locale> validateAndGetLocale(LocaleId locale) {
        return resourceContainer.localeResource.get(locale, new LocaleGetOptions())
    }

    Promise validateOffer(OfferId offer) {
        resourceContainer.offerResource.getOffer(offer.value)
    }

    Promise validateInstrumentUpdateRequest(InstrumentUpdateRequest request) {
        if (request == null) {
            throw AppCommonErrors.INSTANCE.requestBodyRequired().exception()
        }
        notEmpty(request.instrument, 'instrument')
        notEmpty(request.country, 'country')
        if (request.instrument.self == null) { // validate for create
            notEmpty(request.instrument.type, 'instrument.type')
            PIType piType
            try {
                piType = PIType.valueOf(request.instrument.type)
                if (piType != PIType.CREDITCARD) {
                    throw AppCommonErrors.INSTANCE.fieldInvalid('instrument.type', 'Unsupported instrument type.').exception()
                }
            } catch (IllegalArgumentException ex) {
                throw AppCommonErrors.INSTANCE.fieldInvalid('instrument.type', 'Invalid instrument type.').exception()
            }
            notEmpty(request.instrument.accountName, 'instrument.accountName')
            notEmpty(request.instrument.accountNum, 'instrument.accountNum')
            notEmpty(request.instrument.billingAddress, 'instrument.billingAddress')
        }
        return Promise.pure(null)
    }


    Promise validateMakeFreePurchaseRequest(MakeFreePurchaseRequest request) {
        if (request == null) {
            throw AppCommonErrors.INSTANCE.requestBodyRequired().exception()
        }
        notEmpty(request.offer, 'offer')
        notEmpty(request.country, 'country')
        return Promise.pure(null)
    }

    Promise<Void> validatePreparePurchaseRequest(PreparePurchaseRequest request) {
        if (request == null) {
            throw AppCommonErrors.INSTANCE.requestBodyRequired().exception()
        }
        notEmpty(request.offer, 'offerId')
        notEmpty(request.country, 'country')
        if (request.iapParams != null) {
            notEmpty(request.iapParams.packageName, 'iapParams.packageName')
            notEmpty(request.iapParams.packageSignatureHash, 'iapParams.packageSignatureHash')
            notEmpty(request.iapParams.packageVersion, 'iapParams.packageVersion')
        }
        return Promise.pure(null)
    }


    Promise validateCommitPurchaseRequest(CommitPurchaseRequest request) {
        if (request == null) {
            throw AppCommonErrors.INSTANCE.requestBodyRequired().exception()
        }
        notEmpty(request.purchaseToken, 'purchaseToken')
        if (request.challengeAnswer != null) {
            notEmpty(request.challengeAnswer.type, 'challengeSolution.type')
        }
        return Promise.pure(null)
    }

    private static void notEmpty(Object val, String fieldName) {
        if (StringUtils.isEmpty(val)) {
            throw AppCommonErrors.INSTANCE.fieldRequired(fieldName).exception()
        }
    }

    Promise validatePurchaseToken(String purchaseToken, String fieldName) {
        Promise.pure(null).then {
            resourceContainer.orderResource.getOrderByOrderId(new OrderId(IdFormatter.decodeId(OrderId, purchaseToken)))
        }.recover {
            throw AppCommonErrors.INSTANCE.fieldInvalid(fieldName).exception()
        }
    }

    public Promise validateOfferForPurchase(OfferId offerId, CountryId countryId, LocaleId locale, boolean free) {
        return facadeContainer.catalogFacade.getOffer(offerId.value, locale).then { com.junbo.store.spec.model.catalog.Offer offer ->
            if (offer.hasPhysicalItem) {
                throw AppErrors.INSTANCE.invalidOffer('Offer has physical items.').exception()
            }
            if (free && !offer.isFree) {
                throw AppErrors.INSTANCE.invalidOffer('Offer not free.').exception()
            }
            if (!free && offer.isFree) {
                throw AppErrors.INSTANCE.invalidOffer('Offer is free.').exception()
            }
            return Promise.pure()
        }
    }
}
