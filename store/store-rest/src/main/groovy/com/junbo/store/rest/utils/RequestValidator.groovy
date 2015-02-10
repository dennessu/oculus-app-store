package com.junbo.store.rest.utils

import com.junbo.authorization.AuthorizeContext
import com.junbo.common.enumid.CountryId
import com.junbo.common.enumid.LocaleId
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.OfferId
import com.junbo.common.id.OrderId
import com.junbo.common.id.TosId
import com.junbo.common.id.UserId
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.common.model.Results
import com.junbo.identity.spec.v1.model.*
import com.junbo.identity.spec.v1.option.model.CountryGetOptions
import com.junbo.identity.spec.v1.option.model.LocaleGetOptions
import com.junbo.identity.spec.v1.option.model.TosGetOptions
import com.junbo.identity.spec.v1.option.model.UserPersonalInfoGetOptions
import com.junbo.langur.core.context.JunboHttpContext
import com.junbo.langur.core.promise.Promise
import com.junbo.order.spec.model.Order
import com.junbo.payment.spec.model.PaymentInstrument
import com.junbo.payment.spec.model.PaymentInstrumentSearchParam
import com.junbo.store.clientproxy.FacadeContainer
import com.junbo.store.clientproxy.ResourceContainer
import com.junbo.store.clientproxy.error.AppErrorUtils
import com.junbo.store.clientproxy.error.ErrorCodes
import com.junbo.store.rest.purchase.TokenProcessor
import com.junbo.store.spec.error.AppErrors
import com.junbo.store.spec.model.ApiContext
import com.junbo.store.spec.model.Challenge
import com.junbo.store.spec.model.ChallengeAnswer
import com.junbo.store.spec.model.StoreApiHeader
import com.junbo.store.spec.model.billing.InstrumentDeleteRequest
import com.junbo.store.spec.model.billing.InstrumentUpdateRequest
import com.junbo.store.spec.model.browse.AcceptTosRequest
import com.junbo.store.spec.model.browse.DeliveryRequest
import com.junbo.store.spec.model.browse.DetailsRequest
import com.junbo.store.spec.model.browse.ReviewsRequest
import com.junbo.store.spec.model.browse.document.Offer
import com.junbo.store.spec.model.iap.IAPConsumeItemRequest
import com.junbo.store.spec.model.identity.UserProfileUpdateRequest
import com.junbo.store.spec.model.identity.UserProfileUpdateResponse
import com.junbo.store.spec.model.login.*
import com.junbo.store.spec.model.purchase.CommitPurchaseRequest
import com.junbo.store.spec.model.purchase.MakeFreePurchaseRequest
import com.junbo.store.spec.model.purchase.PreparePurchaseRequest
import groovy.transform.CompileStatic
import org.apache.commons.collections.CollectionUtils
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

    @Value('${store.emailVerificationCheck.enabled}')
    private boolean emailVerificationCheckEnabled

    @Resource(name = 'storeResourceContainer')
    ResourceContainer resourceContainer

    @Resource(name = 'storeFacadeContainer')
    private FacadeContainer facadeContainer

    @Resource(name = 'storeIdentityUtils')
    private IdentityUtils identityUtils

    @Resource(name = 'storeTokenProcessor')
    private TokenProcessor tokenProcessor

    @Resource(name = 'storeAppErrorUtils')
    private AppErrorUtils appErrorUtils

    RequestValidator validateRequiredApiHeaders() {
        validateHeader(StoreApiHeader.ANDROID_ID, StoreApiHeader.USER_AGENT, StoreApiHeader.ACCEPT_LANGUAGE)
        return this
    }

    RequestValidator validateEmailCheckRequest(EmailCheckRequest request) {
        if (request == null) {
            throw AppCommonErrors.INSTANCE.requestBodyRequired().exception()
        }

        if (StringUtils.isEmpty(request.email)) {
            throw AppCommonErrors.INSTANCE.fieldRequired('email').exception()
        }

        return this
    }

    RequestValidator validateUsernameAvailableCheckRequest(UserNameCheckRequest request) {
        if (request == null) {
            throw AppCommonErrors.INSTANCE.requestBodyRequired().exception()
        }

        if (StringUtils.isEmpty(request.email)) {
            throw AppCommonErrors.INSTANCE.fieldRequired('email').exception()
        }

        if (StringUtils.isEmpty(request.username)) {
            throw AppCommonErrors.INSTANCE.fieldRequired('username').exception()
        }

        return this
    }

    RequestValidator validateUserCredentialRateRequest(UserCredentialRateRequest request) {
        if (request == null) {
            throw AppCommonErrors.INSTANCE.requestBodyRequired().exception()
        }

        if (request.userCredential == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('userCredential').exception()
        }

        notEmpty(request.userCredential.type, 'userCredential.type')
        notEmpty(request.userCredential.value, 'userCredential.value')
        return this
    }

    RequestValidator validateCreateUserRequest(CreateUserRequest request) {
        if (request == null) {
            throw AppCommonErrors.INSTANCE.requestBodyRequired().exception()
        }

        notEmpty(request.username, 'username')
        notEmpty(request.email, 'email')
        notEmpty(request.password, 'password')
        notEmpty(request.cor, 'cor')
        notEmpty(request.preferredLocale, 'preferredLocale')
        notEmpty(request.tosAgreed, 'tosAgreed')

        if (requireDetailsForCreate) {
            notEmpty(request.dob, 'dob')
            notEmpty(request.firstName, 'firstName')
            notEmpty(request.lastName, 'lastName')
        }

        // Bug https://oculus.atlassian.net/browse/SER-693, it will always set nickName to username
        request.nickName = request.username
        return this
    }

    RequestValidator validateUserSignInRequest(UserSignInRequest request) {
        if (request == null) {
            throw AppCommonErrors.INSTANCE.requestBodyRequired().exception()
        }

        notEmpty(request.email, 'email')
        notEmpty(request.userCredential, 'userCredential')
        notEmpty(request.userCredential.type, 'userCredential.type')
        notEmpty(request.userCredential.value, 'userCredential.value')

        if (!'PASSWORD'.equalsIgnoreCase(request.userCredential.type)) { //
            throw AppCommonErrors.INSTANCE.fieldInvalid('userCredential.type', 'type must be PASSWORD ').exception()
        }
        if (!request.email.contains('@')) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('email', 'email is incorrect format').exception()
        }
        return this
    }

    RequestValidator validateAuthTokenRequest(AuthTokenRequest request) {
        if (request == null) {
            throw AppCommonErrors.INSTANCE.requestBodyRequired().exception()
        }

        notEmpty(request.refreshToken, 'refreshToken')
        return this
    }

    private Challenge ensurePasswordChallenge(ChallengeAnswer challengeAnswer) {
        if (challengeAnswer == null) {
            return new Challenge(type: Constants.ChallengeType.PASSWORD)
        }

        if (challengeAnswer.type != Constants.ChallengeType.PASSWORD || org.apache.commons.lang3.StringUtils.isEmpty(challengeAnswer.password)) {
            return new Challenge(type: Constants.ChallengeType.PASSWORD)
        }

        return null
    }

    Promise<UserProfileUpdateResponse> validateUserProfileUpdateRequest(UserProfileUpdateRequest request, ApiContext apiContext) {
        if (request == null) {
            throw AppCommonErrors.INSTANCE.requestBodyRequired().exception()
        }

        notEmpty(request.userProfile, 'userProfile')
        // https://oculus.atlassian.net/browse/SER-693
        // Will disable nickname update, nickname should be the username, if user want to update nickname, he should update username
        request.userProfile.nickName = request.userProfile.username

        return getUserProfileUpdatePasswordChallenge(request).then { UserProfileUpdateResponse response ->
            if (response != null) {
                return Promise.pure(response)
            }

            return userProfileUpdateEmailVerification(request, apiContext).then {
                return Promise.pure(null)
            }
        }
    }

    Promise<UserProfileUpdateResponse> getUserProfileUpdatePasswordChallenge(UserProfileUpdateRequest request) {
        return identityUtils.getVerifiedUserFromToken().then { User user ->
            return askUserProfileUpdatePasswordChallenge(request, user).then { Boolean askChallenge ->
                if (askChallenge) {
                    Challenge challenge = ensurePasswordChallenge(request.challengeAnswer)
                    if (challenge != null) {
                        return Promise.pure(new UserProfileUpdateResponse(challenge: challenge))
                    }

                    return Promise.pure(null)
                } else {
                    return Promise.pure(null)
                }
            }.then { UserProfileUpdateResponse response ->
                if (response != null) {
                    return Promise.pure(response)
                }

                if (request?.challengeAnswer?.password != null && request?.challengeAnswer?.type == Constants.ChallengeType.PASSWORD) {
                    return identityUtils.getVerifiedUserPrimaryMail(user).then { String username ->
                        return resourceContainer.userCredentialVerifyAttemptResource.create(new UserCredentialVerifyAttempt(
                                username: username,
                                value: request.challengeAnswer.password,
                                type: Constants.CredentialType.PASSWORD
                        )).recover { Throwable t ->
                            if (appErrorUtils.isAppError(t, ErrorCodes.Identity.UserPasswordIncorrect)) {
                               throw AppErrors.INSTANCE.invalidChallengeAnswer().exception()
                            }
                            if (appErrorUtils.isAppError(t, ErrorCodes.Identity.MaximumLoginAttempt)) {
                                throw AppErrors.INSTANCE.invalidChallengeAnswer().exception()
                            }

                            appErrorUtils.throwUnknownError('updateUserProfile', t)
                        }.then {
                            return Promise.pure(null)
                        }
                    }
                }

                return Promise.pure(null)
            }
        }
    }

    Promise<Void> userProfileUpdateEmailVerification(UserProfileUpdateRequest request, ApiContext apiContext) {
        return identityUtils.getVerifiedUserFromToken().then { User user ->
            LocaleId locale = user.preferredLocale != null ? user.preferredLocale : apiContext.locale.getId()
            CountryId country = user.countryOfResidence != null ? user.countryOfResidence : apiContext.country.getId()
            return isMailChanged(request, user).then { Boolean mailChanged ->
                if (mailChanged) {
                    Email email = new Email(
                            info: request.userProfile?.email?.value
                    )
                    UserPersonalInfo emailPii = new UserPersonalInfo(
                            userId: user.getId(),
                            type: 'EMAIL',
                            value: ObjectMapperProvider.instanceNotStrict().valueToTree(email)
                    )
                    return resourceContainer.userUserPersonalInfoResource.create(emailPii).then { UserPersonalInfo userPersonalInfo ->
                        return facadeContainer.oAuthFacade.sendVerifyEmail(locale.value, country.value, user.getId(), userPersonalInfo.getId()).then {
                            return Promise.pure(null)
                        }
                    }
                }

                return Promise.pure(null)
            }
        }
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

    Promise<Tos> validateTosExists(TosId tosId) {
        return resourceContainer.tosResource.get(tosId, new TosGetOptions()).then { Tos tos ->
            if (tos == null || tos.state != 'APPROVED') {
                throw AppCommonErrors.INSTANCE.fieldInvalid('tosAgreed', 'Tos with Id ' + tos.id.toString() + ' not exists').exception()
            }

            return Promise.pure(tos)
        }
    }

    Promise validateOffer(OfferId offer) {
        resourceContainer.offerResource.getOffer(offer.value)
    }

    Promise validateInstrumentUpdateRequest(InstrumentUpdateRequest request) {
        if (request == null) {
            throw AppCommonErrors.INSTANCE.requestBodyRequired().exception()
        }
        notEmpty(request.instrument, 'instrument')
        if (request.instrument.self == null) { // validate for create
            notEmpty(request.instrument.type, 'instrument.type')
            com.junbo.common.id.PIType piType
            try {
                piType = com.junbo.common.id.PIType.valueOf(request.instrument.type)
                if (piType != com.junbo.common.id.PIType.CREDITCARD && piType != com.junbo.common.id.PIType.STOREDVALUE) {
                    throw AppCommonErrors.INSTANCE.fieldInvalid('instrument.type', 'Unsupported instrument type.').exception()
                }
            } catch (IllegalArgumentException ex) {
                throw AppCommonErrors.INSTANCE.fieldInvalid('instrument.type', 'Invalid instrument type.').exception()
            }
            if (piType == com.junbo.common.id.PIType.CREDITCARD) {
                notEmpty(request.instrument.accountName, 'instrument.accountName')
                notEmpty(request.instrument.accountNum, 'instrument.accountNum')
                notEmpty(request.instrument.billingAddress, 'instrument.billingAddress')
            } else if (piType == com.junbo.common.id.PIType.STOREDVALUE) {
                notEmpty(request.instrument.storedValueCurrency, 'instrument.storedValueCurrency')
                notEmpty(request.instrument.billingAddress, 'instrument.billingAddress')
            } else {
                throw AppCommonErrors.INSTANCE.fieldInvalid('instrument.type', 'Invalid instruent type.').exception()
            }
        }
        return Promise.pure(null)
    }

    Promise validateInstrumentDeleteRequest(User user, InstrumentDeleteRequest request) {
        if (request == null) {
            throw AppCommonErrors.INSTANCE.requestBodyRequired().exception()
        }
        notEmpty(request.self, 'paymentInstrumentId')

        resourceContainer.paymentInstrumentResource.getById(request.self).then { PaymentInstrument paymentInstrument ->
            if (paymentInstrument == null) {
                throw AppCommonErrors.INSTANCE.parameterInvalid('paymentInstrumentId', 'payment Instrument does not exist.').exception()
            }

            if (!user.getId().getValue().equals(paymentInstrument.getUserId())) {
                throw AppCommonErrors.INSTANCE.forbiddenWithMessage("user does not own this payment instrument").exception()
            }

            return Promise.pure()
        }.recover { Throwable throwable ->
            throw AppCommonErrors.INSTANCE.parameterInvalid('paymentInstrumentId', 'payment Instrument is not valid.').exception()
        }
    }

    void validateMakeFreePurchaseRequest(MakeFreePurchaseRequest request) {
        if (request == null) {
            throw AppCommonErrors.INSTANCE.requestBodyRequired().exception()
        }
        notEmpty(request.offer?.value, 'offer')
    }

    void validatePreparePurchaseRequest(PreparePurchaseRequest request) {
        if (request == null) {
            throw AppCommonErrors.INSTANCE.requestBodyRequired().exception()
        }
        notEmpty(request.offer?.value, 'offerId')
        if (request.challengeAnswer != null) {
            if (request.challengeAnswer.type == Constants.ChallengeType.PIN) {
                notEmpty(request.challengeAnswer.pin, 'challengeAnswer.pin')
            } else if (request.challengeAnswer.type == Constants.ChallengeType.TOS_ACCEPTANCE) {
                notEmpty(request.challengeAnswer.acceptedTos, 'challengeAnswer.acceptedTos')
            } else if (request.challengeAnswer.type == Constants.ChallengeType.PASSWORD) {
                notEmpty(request.challengeAnswer.password, 'challengeAnswer.password')
            }
        }
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

        if (val instanceof String) {
            String value = (String)val;
            if (StringUtils.isEmpty(value.trim())) {
                throw AppCommonErrors.INSTANCE.fieldRequired(fieldName).exception()
            }
        }
    }

    Promise validateOrderValid(OrderId orderId) {
        Promise.pure(null).then {
            resourceContainer.orderResource.getOrderByOrderId(orderId).then { Order order ->
                if (order.user == null || order.user != AuthorizeContext.currentUserId) {
                    throw AppCommonErrors.INSTANCE.fieldInvalid('purchaseToken').exception()
                }
                return Promise.pure(null)
            }
        }.recover {
            throw AppCommonErrors.INSTANCE.fieldInvalid('purchaseToken').exception()
        }
    }

    public Promise validateOfferForPurchase(UserId userId, Offer offer, boolean free) {
        if (offer.hasPhysicalItem) {
            throw AppErrors.INSTANCE.invalidOffer('Offer has physical items.').exception()
        }
        if (free && !offer.isFree) {
            throw AppErrors.INSTANCE.invalidOffer('Offer not free.').exception()
        }
        if (!free && offer.isFree) {
            throw AppErrors.INSTANCE.invalidOffer('Offer is free.').exception()
        }
        if (offer.hasStoreValueItem) {
            return resourceContainer.paymentInstrumentResource.searchPaymentInstrument(new PaymentInstrumentSearchParam(
                    userId: userId,
                    type: com.junbo.common.id.PIType.STOREDVALUE.toString()
            )).then { Results<PaymentInstrument> results ->
                if (results == null || CollectionUtils.isEmpty(results.items)) {
                    throw AppErrors.INSTANCE.invalidOffer('StoreValue is not exists.').exception()
                }

                return Promise.pure(null)
            }
        }
        return Promise.pure()
    }

    public void validateAcceptTosRequest(AcceptTosRequest request) {
        if (request == null) {
            throw AppCommonErrors.INSTANCE.requestBodyRequired().exception()
        }
        notEmpty(request.tosId, 'tosId')
    }

    public RequestValidator validateRequestBody(Object request) {
        if (request == null) {
            throw AppCommonErrors.INSTANCE.requestBodyRequired().exception()
        }
        return this
    }

    public void validateDetailsRequest(DetailsRequest request) {
        if (request == null) {
            throw AppCommonErrors.INSTANCE.requestBodyRequired().exception()
        }
        notEmpty(request.itemId, 'itemId')
    }

    public void validateHeader(StoreApiHeader... headers) {
        for (StoreApiHeader header: headers) {
            String value = JunboHttpContext.requestHeaders.getFirst(header.value)
            if (StringUtils.isEmpty(value)) {
                throw AppCommonErrors.INSTANCE.headerRequired(header.value).exception()
            }
        }
    }

    public void validateDeliveryRequest(DeliveryRequest request, boolean fromOffer) {
        if (!fromOffer) {
            notEmpty(request.itemId, 'itemId')
        } else {
            notEmpty(request.offerId, 'offerId')
        }

    }

    public void validateReviewsRequest(ReviewsRequest request) {
        notEmpty(request.itemId, 'itemId')
    }

    public void validateConfirmEmailRequest(ConfirmEmailRequest request) {
        notEmpty(request.evc, 'evc')
    }

    public void validateIAPConsumeItemRequest(IAPConsumeItemRequest iapConsumeItemRequest) {
        notEmpty(iapConsumeItemRequest.sku, 'sku')
        notEmpty(iapConsumeItemRequest.trackingGuid, 'trackingGuid')
        notEmpty(iapConsumeItemRequest.useCountConsumed, 'useCountConsumed')
    }

    private Promise<Boolean> isMailChanged(UserProfileUpdateRequest request, User currentUser) {
        if (StringUtils.isEmpty(request.userProfile?.email?.value)) {
            return Promise.pure(false)
        }

        if (CollectionUtils.isEmpty(currentUser.emails)) {
            return Promise.pure(false)
        }
        UserPersonalInfoLink defaultLink = currentUser.emails.find { UserPersonalInfoLink link ->
            return link.isDefault
        }
        if (defaultLink == null) {
            return Promise.pure(false)
        }

        return resourceContainer.userPersonalInfoResource.get(defaultLink.value, new UserPersonalInfoGetOptions()).then { UserPersonalInfo userPersonalInfo ->
            if (userPersonalInfo == null || userPersonalInfo.lastValidateTime == null) {
                return Promise.pure(false)
            }   

            Email email = (Email)ObjectMapperProvider.instanceNotStrict().treeToValue(userPersonalInfo.value, Email)
            if (email.info == request.userProfile?.email?.value){
                return Promise.pure(false)
            }

            return Promise.pure(true)
        }
    }

    private boolean isPinChanged(UserProfileUpdateRequest request) {
        return !org.apache.commons.lang3.StringUtils.isEmpty(request.userProfile?.pin)
    }

    private boolean isPasswordChanged(UserProfileUpdateRequest request) {
        return !org.apache.commons.lang3.StringUtils.isEmpty(request.userProfile?.password)
    }

    private Promise<Boolean> askUserProfileUpdatePasswordChallenge(UserProfileUpdateRequest request, User currentUser) {
        boolean askChallenge = isPinChanged(request) || isPasswordChanged(request)
        if (askChallenge) {
            return Promise.pure(askChallenge)
        }

        return isMailChanged(request, currentUser)
    }
}
