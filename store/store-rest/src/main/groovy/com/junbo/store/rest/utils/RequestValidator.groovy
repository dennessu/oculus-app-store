package com.junbo.store.rest.utils
import com.junbo.authorization.AuthorizeContext
import com.junbo.common.enumid.CountryId
import com.junbo.common.enumid.LocaleId
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.error.AppError
import com.junbo.common.id.OfferId
import com.junbo.common.id.OrderId
import com.junbo.common.id.PIType
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.identity.spec.v1.model.*
import com.junbo.identity.spec.v1.option.model.CountryGetOptions
import com.junbo.identity.spec.v1.option.model.LocaleGetOptions
import com.junbo.identity.spec.v1.option.model.UserPersonalInfoGetOptions
import com.junbo.langur.core.context.JunboHttpContext
import com.junbo.langur.core.promise.Promise
import com.junbo.store.clientproxy.FacadeContainer
import com.junbo.store.rest.purchase.TokenProcessor
import com.junbo.store.spec.error.AppErrors
import com.junbo.store.spec.model.Challenge
import com.junbo.store.spec.model.ChallengeAnswer
import com.junbo.store.spec.model.StoreApiHeader
import com.junbo.store.spec.model.billing.InstrumentUpdateRequest
import com.junbo.store.spec.model.browse.AcceptTosRequest
import com.junbo.store.spec.model.browse.DeliveryRequest
import com.junbo.store.spec.model.browse.DetailsRequest
import com.junbo.store.spec.model.identity.UserProfileUpdateRequest
import com.junbo.store.spec.model.identity.UserProfileUpdateResponse
import com.junbo.store.spec.model.login.*
import com.junbo.store.spec.model.profile.UpdateProfileState
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
        validateHeader(StoreApiHeader.ANDROID_ID, StoreApiHeader.USER_AGENT, StoreApiHeader.ACCEPT_LANGUAGE, StoreApiHeader.MCCMNC)
        return this
    }

    RequestValidator validateUserNameCheckRequest(UserNameCheckRequest request) {
        if (request == null) {
            throw AppCommonErrors.INSTANCE.requestBodyRequired().exception()
        }

        if (StringUtils.isEmpty(request.email) && StringUtils.isEmpty(request.username)) {
            throw AppCommonErrors.INSTANCE.fieldRequired('email or username').exception()
        }

        if (!StringUtils.isEmpty(request.email) && !StringUtils.isEmpty(request.username)) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('email or username', 'only one of the fields [email, username] could be specified at a time').exception()
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

        if (requireDetailsForCreate) {
            notEmpty(request.pin, 'pin')
            notEmpty(request.dob, 'dob')
            notEmpty(request.firstName, 'firstName')
            notEmpty(request.lastName, 'lastName')
        }
        return this
    }

    RequestValidator validateUserSignInRequest(UserSignInRequest request) {
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

    private Promise<Challenge> ensureEmailVerificationChallenge(UserProfileUpdateRequest request, User user) {
        // todo:    Need to add locale, country and etc
        // check whether updateProfileToken has token
        return isEmailPIICreated(request).then { Boolean mailPIICreated ->
            if (!mailPIICreated) {
                return Promise.pure(new Challenge(type: Constants.ChallengeType.EMAIL_VERIFICATION))
            } else {
                // check this mail is verified and can be replaced
                return tokenProcessor.toTokenObject(request.userProfileUpdateToken, UpdateProfileState).then { UpdateProfileState state ->
                    return resourceContainer.userPersonalInfoResource.get(state.getEmailPIIId(), new UserPersonalInfoGetOptions()).then {
                        UserPersonalInfo userPersonalInfo ->
                        if (userPersonalInfo.lastValidateTime != null) {
                            return Promise.pure(null)
                        }

                        return Promise.pure(new Challenge(type: Constants.ChallengeType.EMAIL_VERIFICATION))
                    }
                }
            }
        }
    }

    private Promise<Boolean> isEmailPIICreated(UserProfileUpdateRequest request) {
        if (StringUtils.isEmpty(request.userProfileUpdateToken)) {
            return Promise.pure(false)
        } else {
            return tokenProcessor.toTokenObject(request.userProfileUpdateToken, UpdateProfileState).then { UpdateProfileState state ->
                if (state.emailPIIId != null) {
                    return Promise.pure(true)
                }

                return Promise.pure(false)
            }
        }
    }

    Promise<UserProfileUpdateResponse> validateUserProfileUpdateRequest(UserProfileUpdateRequest request) {
        if (request == null) {
            throw AppCommonErrors.INSTANCE.requestBodyRequired().exception()
        }

        notEmpty(request.userProfile, 'userProfile')
        if (!StringUtils.isEmpty(request.userProfileUpdateToken)) {
            return tokenProcessor.toTokenObject(request.userProfileUpdateToken, UpdateProfileState).then { UpdateProfileState state ->
                if (state.userId != AuthorizeContext.currentUserId) {
                    throw AppErrors.INSTANCE.invalidUpdateProfileToken().exception()
                }

                return  getUserProfileUpdateEmailVerificationChallenge(request).then { UserProfileUpdateResponse response ->
                    if (response != null) {
                        return Promise.pure(response)
                    }

                    return getUserProfileUpdatePasswordChallenge(request)
                }
            }
        }

        return getUserProfileUpdateEmailVerificationChallenge(request).then { UserProfileUpdateResponse response ->
            if (response != null) {
                return Promise.pure(response)
            }

            return getUserProfileUpdatePasswordChallenge(request)
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
                    if (!StringUtils.isEmpty(request.getUserProfileUpdateToken())) {
                        return tokenProcessor.toTokenObject(request.getUserProfileUpdateToken(), UpdateProfileState).then { UpdateProfileState state ->
                            UpdateProfileState updateProfileState = new UpdateProfileState(
                                    userId: AuthorizeContext.currentUserId,
                                    emailPIIId: state.emailPIIId,
                                    timeStamp: new Date()
                            )

                            return tokenProcessor.toTokenString(updateProfileState).then { String userProfileUpdateToken ->
                                response.setUserProfileUpdateToken(userProfileUpdateToken)
                                return Promise.pure(response)
                            }
                        }
                    } else {
                        UpdateProfileState updateProfileState = new UpdateProfileState(
                                userId: AuthorizeContext.currentUserId,
                                timeStamp: new Date()
                        )

                        return tokenProcessor.toTokenString(updateProfileState).then { String userProfileUpdateToken ->
                            response.setUserProfileUpdateToken(userProfileUpdateToken)
                            return Promise.pure(response)
                        }
                    }
                }

                if (request?.challengeAnswer?.password != null && request?.challengeAnswer?.type == Constants.ChallengeType.PASSWORD) {
                    return getUsername(user).then { String username ->
                        return resourceContainer.userCredentialVerifyAttemptResource.create(new UserCredentialVerifyAttempt(
                                username: username,
                                value: request.challengeAnswer.password,
                                type: Constants.CredentialType.PASSWORD
                        )).recover { Throwable t ->
                            if (appErrorUtils.isAppError(t, ErrorCodes.Identity.InvalidPassword)) {
                               throw AppErrors.INSTANCE.invalidChallengeAnswer().exception()
                            }
                            if (appErrorUtils.isAppError(t, ErrorCodes.Identity.InvalidField)) {
                                AppError appError = (AppError)t
                                if (appError.error().message.contains('User reaches maximum allowed retry count')) {
                                    throw AppErrors.INSTANCE.maximumAttemptReached().exception()
                                }
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

    Promise<UserProfileUpdateResponse> getUserProfileUpdateEmailVerificationChallenge(UserProfileUpdateRequest request) {
        return identityUtils.getVerifiedUserFromToken().then { User user ->
            return askUserProfileUpdateEmailVerificationChallenge(request, user).then { Boolean askChallenge ->
                if (askChallenge) {
                    return ensureEmailVerificationChallenge(request, user).then { Challenge challenge ->
                        if (challenge == null) {
                            return Promise.pure(null)
                        }
                        return Promise.pure(new UserProfileUpdateResponse(challenge: challenge))
                    }
                } else {
                    return Promise.pure(null)
                }
            }.then { UserProfileUpdateResponse response ->
                if (response != null) {
                    return isEmailPIICreated(request).then { Boolean mailPIICreated ->
                        if (mailPIICreated) {
                            return Promise.pure(response)
                        }

                        Email email = new Email(
                                info: request.userProfile?.email?.value
                        )
                        UserPersonalInfo emailPii = new UserPersonalInfo(
                                userId: user.getId(),
                                type: 'EMAIL',
                                value: ObjectMapperProvider.instance().valueToTree(email)
                        )
                        return resourceContainer.userPersonalInfoResource.create(emailPii).then { UserPersonalInfo created ->
                            UpdateProfileState updateProfileState = new UpdateProfileState(
                                    userId: user.getId(),
                                    emailPIIId: created.getId(),
                                    timeStamp: new Date()
                            )

                            return resourceContainer.emailVerifyEndpoint.sendVerifyEmail('en_US', 'US', user.getId(), created.getId()).then {
                                return tokenProcessor.toTokenString(updateProfileState).then { String userProfileUpdateToken ->
                                    response.setUserProfileUpdateToken(userProfileUpdateToken)
                                    return Promise.pure(response)
                                }
                            }
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
        return Promise.pure(null)
    }

    Promise<Void> validatePreparePurchaseRequest(PreparePurchaseRequest request) {
        if (request == null) {
            throw AppCommonErrors.INSTANCE.requestBodyRequired().exception()
        }
        notEmpty(request.offer, 'offerId')
        if (request.iapParams != null) {
            notEmpty(request.iapParams.packageName, 'iapParams.packageName')
            notEmpty(request.iapParams.packageSignatureHash, 'iapParams.packageSignatureHash')
            notEmpty(request.iapParams.packageVersion, 'iapParams.packageVersion')
        }
        if (request.challengeAnswer != null) {
            if (request.challengeAnswer.type == Constants.ChallengeType.PIN) {
                notEmpty(request.challengeAnswer.pin, 'challengeAnswer.pin')
            } else if (request.challengeAnswer.type == Constants.ChallengeType.TOS_ACCEPTANCE) {
                notEmpty(request.challengeAnswer.acceptedTos, 'challengeAnswer.acceptedTos')
            } else if (request.challengeAnswer.type == Constants.ChallengeType.PASSWORD) {
                notEmpty(request.challengeAnswer.password, 'challengeAnswer.password')
            } else if (request.challengeAnswer.type != Constants.ChallengeType.EMAIL_VERIFICATION) {
                throw AppCommonErrors.INSTANCE.fieldInvalid('challengeAnswer.type').exception()
            }
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

    Promise validateOrderValid(OrderId orderId) {
        Promise.pure(null).then {
            resourceContainer.orderResource.getOrderByOrderId(orderId)
        }.recover {
            throw AppCommonErrors.INSTANCE.fieldInvalid('purchaseToken').exception()
        }
    }

    public Promise validateOfferForPurchase(OfferId offerId, CountryId countryId, LocaleId locale, boolean free) {
        return facadeContainer.catalogFacade.getOffer(offerId.value, locale).then { com.junbo.store.spec.model.catalog.Offer offer ->
            // todo:    Here we may need to call rating to determine whether this is free or not
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

    public void validateAcceptTosRequest(AcceptTosRequest request) {
        if (request == null) {
            throw AppCommonErrors.INSTANCE.requestBodyRequired().exception()
        }
        notEmpty(request.tosId, 'tosId')
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

    public void validateDeliveryRequest(DeliveryRequest request) {
        notEmpty(request.itemId, 'itemId')
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

            Email email = (Email)ObjectMapperProvider.instance().treeToValue(userPersonalInfo.value, Email)
            if (email.info == request.userProfile?.email?.value){
                return Promise.pure(false)
            }

            return getCreatedVerifiedEmail(request).then { Email mail ->
                if (mail == null) {
                    return Promise.pure(true)
                }

                if (mail.info != request.userProfile?.email?.value) {
                    throw AppErrors.INSTANCE.invalidUpdateProfileToken().exception()
                }

                return Promise.pure(true)
            }
        }
    }

    private Promise<Email> getCreatedVerifiedEmail(UserProfileUpdateRequest request) {
        if (StringUtils.isEmpty(request.userProfileUpdateToken)) {
            return Promise.pure(null)
        }

        return tokenProcessor.toTokenObject(request.userProfileUpdateToken, UpdateProfileState).then { UpdateProfileState state ->
            if (state.emailPIIId == null) {
                return Promise.pure(null)
            }

            return resourceContainer.userPersonalInfoResource.get(state.emailPIIId, new UserPersonalInfoGetOptions()).then { UserPersonalInfo mailPII ->
                if (mailPII == null) {
                    return Promise.pure(null)
                }

                if (mailPII.lastValidateTime == null) {
                    return Promise.pure(null)
                }

                Email email = (Email)ObjectMapperProvider.instance().treeToValue(mailPII.value, Email)
                return Promise.pure(email)
            }
        }
    }

    private Promise<String> getUsername(User user) {
        if (user.username == null) {
            return Promise.pure('')
        }

        return resourceContainer.userPersonalInfoResource.get(user.username, new UserPersonalInfoGetOptions()).then { UserPersonalInfo userPersonalInfo ->
            if (userPersonalInfo == null) {
                return Promise.pure('')
            }

            UserLoginName loginName = (UserLoginName)ObjectMapperProvider.instance().treeToValue(userPersonalInfo.value, UserLoginName)
            return Promise.pure(loginName.userName)
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

    private Promise<Boolean> askUserProfileUpdateEmailVerificationChallenge(UserProfileUpdateRequest request, User currentUser) {
        return isMailChanged(request, currentUser)
    }
}
