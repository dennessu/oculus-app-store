package com.junbo.store.rest.resource
import com.fasterxml.jackson.databind.JsonNode
import com.junbo.common.enumid.CountryId
import com.junbo.common.enumid.LocaleId
import com.junbo.common.id.UserId
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.common.model.Results
import com.junbo.common.util.IdFormatter
import com.junbo.identity.spec.v1.model.*
import com.junbo.identity.spec.v1.option.list.CommunicationListOptions
import com.junbo.identity.spec.v1.option.list.TosListOptions
import com.junbo.identity.spec.v1.option.model.UserGetOptions
import com.junbo.identity.spec.v1.option.model.UserPersonalInfoGetOptions
import com.junbo.langur.core.promise.Promise
import com.junbo.oauth.spec.model.*
import com.junbo.store.clientproxy.ResourceContainer
import com.junbo.store.clientproxy.error.AppErrorUtils
import com.junbo.store.clientproxy.error.ErrorCodes
import com.junbo.store.clientproxy.error.ErrorContext
import com.junbo.store.clientproxy.sentry.SentryFacade
import com.junbo.store.common.utils.CommonUtils
import com.junbo.store.rest.challenge.ChallengeHelper
import com.junbo.store.rest.utils.ApiContextBuilder
import com.junbo.store.rest.utils.DataConverter
import com.junbo.store.rest.utils.InitialItemPurchaseUtils
import com.junbo.store.rest.utils.RequestValidator
import com.junbo.store.spec.model.Challenge
import com.junbo.store.spec.model.identity.StoreUserEmail
import com.junbo.store.spec.model.login.*
import com.junbo.store.spec.resource.LoginResource
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.util.CollectionUtils

import javax.annotation.Resource
import javax.ws.rs.ext.Provider

@Provider
@CompileStatic
@Component('defaultLoginResource')
class LoginResourceImpl implements LoginResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginResourceImpl)

    private static final String AUTH_HEADER = 'Authorization'

    private static final String COMMUNICATION_DEFAULT_LOCALE = 'en_US'

    @Value('${store.login.requireDetailsForCreate}')
    private boolean requireDetailsForCreate

    @Value('${store.oauth.clientId}')
    private String clientId

    @Value('${store.oauth.clientSecret}')
    private String clientSecret

    @Value('${store.oauth.defaultScopes}')
    private String defaultScopes

    @Resource(name = 'storeResourceContainer')
    private ResourceContainer resourceContainer

    @Resource(name = 'storeRequestValidator')
    private RequestValidator requestValidator

    @Resource(name = 'storeAppErrorUtils')
    private AppErrorUtils appErrorUtils

    @Resource(name = 'storeContextBuilder')
    private ApiContextBuilder apiContextBuilder

    @Resource(name = 'storeSentryFacade')
    private SentryFacade sentryFacade

    @Value('${store.tos.createuser}')
    private String tosCreateUser

    @Resource(name = 'storeDataConverter')
    DataConverter dataConverter

    @Resource(name = 'storeChallengeHelper')
    private ChallengeHelper challengeHelper

    @Resource(name = 'storeInitialItemPurchaseUtils')
    private InitialItemPurchaseUtils initialItemPurchaseUtils

    @Value('${store.communication.createuser}')
    private String registerUserCommunication

    private class ApiContext {
        User user
    }

    @Override
    Promise<EmailCheckResponse> checkEmail(EmailCheckRequest emailCheckRequest) {
        requestValidator.validateRequiredApiHeaders().validateEmailCheckRequest(emailCheckRequest)
        EmailCheckResponse response
        ErrorContext errorContext = new ErrorContext()

        Promise.pure().then {
            errorContext.fieldName = 'email'
            return resourceContainer.userResource.checkEmail(emailCheckRequest.email)
        }.recover { Throwable ex ->
            if (appErrorUtils.isAppError(ex, ErrorCodes.Identity.FieldDuplicate)) {
                response = new EmailCheckResponse(isAvailable : false)
                return Promise.pure()
            }
            appErrorUtils.throwOnFieldInvalidError(errorContext, ex, ErrorCodes.Identity.majorCode)
            appErrorUtils.throwUnknownError('checkEmail', ex)
        }.then {
            if (response == null) {
                return Promise.pure(new EmailCheckResponse(isAvailable: true))
            }
            return Promise.pure(response)
        }
    }

    @Override
    Promise<UserNameCheckResponse> checkUsernameAvailable(UserNameCheckRequest userNameCheckRequest) {
        requestValidator.validateRequiredApiHeaders().validateUsernameAvailableCheckRequest(userNameCheckRequest)
        UserNameCheckResponse response
        ErrorContext errorContext = new ErrorContext()

        Promise.pure().then {
            errorContext.fieldName = 'email'
            return resourceContainer.userResource.checkEmail(userNameCheckRequest.email)
        }.recover { Throwable ex ->
            if (appErrorUtils.isAppError(ex, ErrorCodes.Identity.FieldDuplicate)) {
                response = new UserNameCheckResponse(isAvailable : false)
                return Promise.pure()
            }
            appErrorUtils.throwOnFieldInvalidError(errorContext, ex, ErrorCodes.Identity.majorCode)
            appErrorUtils.throwUnknownError('checkUsernameAvailable', ex)
        }.then {
            if (response == null) {
                errorContext.fieldName = 'username'
                return resourceContainer.userResource.checkUsername(userNameCheckRequest.username).then {
                    return resourceContainer.userResource.checkUsernameEmailBlocker(userNameCheckRequest.username, userNameCheckRequest.email).then { Boolean blocker ->
                        if (blocker) {
                            response = new UserNameCheckResponse(isAvailable: false)
                        }

                        return Promise.pure(null)
                    }
                }
            }
            return Promise.pure(null)
        }.recover { Throwable ex ->
            if (appErrorUtils.isAppError(ex, ErrorCodes.Identity.FieldDuplicate)) {
                response = new UserNameCheckResponse(isAvailable: false)
                return Promise.pure()
            }
            appErrorUtils.throwOnFieldInvalidError(errorContext, ex, ErrorCodes.Identity.majorCode)
            appErrorUtils.throwUnknownError('checkUsernameAvailable', ex)
        }.then {
            if (response == null) {
                return Promise.pure(new UserNameCheckResponse(isAvailable: true))
            }
            return Promise.pure(response)
        }
    }

    @Override
    Promise<UserCredentialRateResponse> rateUserCredential(UserCredentialRateRequest request) {
        requestValidator.validateRequiredApiHeaders().validateUserCredentialRateRequest(request)

        return resourceContainer.userCredentialResource.rateCredential(new RateUserCredentialRequest(
                type: request.userCredential.type,
                value: request.userCredential.value,
                context: new RateUserCredentialContext(
                        username: request.context?.username
                )
        )).then { RateUserCredentialResponse response ->
            return Promise.pure(new UserCredentialRateResponse(
                    strength: response.strength
            ))
        }
    }

    @Override
    Promise<AuthTokenResponse> createUser(CreateUserRequest request) {
        com.junbo.store.spec.model.ApiContext apc
        requestValidator.validateRequiredApiHeaders().validateCreateUserRequest(request)
        ApiContext apiContext = new ApiContext()
        ErrorContext errorContext = new ErrorContext()
        StoreUserEmail storeUserEmail
        AuthTokenResponse authTokenResponse

        return apiContextBuilder.buildApiContext().then { com.junbo.store.spec.model.ApiContext storeApiContext ->
            apc = storeApiContext
            return Promise.pure(null)
        }.then {
            requestValidator.validateAndGetCountry(new CountryId(request.cor))
        }.then {
            requestValidator.validateAndGetLocale(new LocaleId(request.preferredLocale))
        }.then {
            requestValidator.validateTosExists(request.tosAgreed)
        }.then {
            // todo:    Here we need to define the parameters here
            sentryFacade.doSentryCheck(null, null, null, null, null, null)
        }.then {
            createUserBasic(request, apiContext, errorContext)
        }.then {
            createUserPersonalInfo(request, apiContext, errorContext).then { StoreUserEmail email ->
                storeUserEmail = email
                return Promise.pure()
            }
        }.then {
            return resourceContainer.userResource.put(apiContext.user.getId(), apiContext.user).then { User u ->
                apiContext.user = u
                return Promise.pure()
            }
        }.recover { Throwable ex ->
            def promise = Promise.pure()

            if (apiContext.user?.getId() != null) {
                promise = rollBackUser(apiContext.user)
            }

            promise.then {
                appErrorUtils.throwOnFieldInvalidError(errorContext, ex, ErrorCodes.Identity.majorCode)
                if (appErrorUtils.isAppError(ex, ErrorCodes.Identity.CountryNotFound,
                        ErrorCodes.Identity.LocaleNotFound, ErrorCodes.Identity.InvalidPassword,
                        ErrorCodes.Identity.FieldDuplicate, ErrorCodes.Identity.AgeRestriction)) {
                    throw ex
                }
                appErrorUtils.throwUnknownError('createUser', ex)
            }
        }.then {
            // get the auth token
            innerSignIn(request.email, request.password).then { AuthTokenResponse response ->
                authTokenResponse = response
                CommonUtils.pushAuthHeader(authTokenResponse.accessToken)
                Promise.pure().then { // create user tos using the access token
                    return resourceContainer.userTosAgreementResource.create(new UserTosAgreement(userId: apiContext.user.getId(),
                            tosId: request.tosAgreed,
                            agreementTime: new Date()))
                }.then {
                    createUserCommunication(request, apiContext.user.getId(), apc) // create user communication using the access token
                }.then {
                    initialItemPurchaseUtils.checkAndPurchaseInitialOffers(authTokenResponse.userId, true, apc)
                }.recover { Throwable ex ->
                    CommonUtils.popAuthHeader()
                    throw ex
                }.then {
                    CommonUtils.popAuthHeader()
                    return Promise.pure()
                }
            }
        }.then {
            return resourceContainer.emailVerifyEndpoint.sendWelcomeEmail(request.preferredLocale, request.cor, apiContext.user.getId()).then {
                return Promise.pure(authTokenResponse)
            }
        }
    }

    @Override
    Promise<AuthTokenResponse> signIn(UserSignInRequest userSignInRequest) {
        com.junbo.store.spec.model.ApiContext apc
        requestValidator.validateRequiredApiHeaders().validateUserSignInRequest(userSignInRequest)

        return apiContextBuilder.buildApiContext().then { com.junbo.store.spec.model.ApiContext apiContext ->
            apc = apiContext
            return Promise.pure(null)
        }.then {
            innerSignIn(userSignInRequest.email, userSignInRequest.userCredential.value).recover { Throwable ex ->
                if (appErrorUtils.isAppError(ex, ErrorCodes.OAuth.InvalidCredential)) {
                    throw ex
                }
                appErrorUtils.throwUnknownError('signIn', ex)
            }
        }.then { AuthTokenResponse authTokenResponse ->
            if (authTokenResponse != null) {
                return processAfterSignIn(userSignInRequest, authTokenResponse, apc)
            }
            return Promise.pure(authTokenResponse)
        }
    }

    @Override
    Promise<AuthTokenResponse> getAuthToken(AuthTokenRequest tokenRequest) {
        requestValidator.validateRequiredApiHeaders().validateAuthTokenRequest(tokenRequest)

        return resourceContainer.tokenEndpoint.postToken(
                new AccessTokenRequest(
                        refreshToken: tokenRequest.refreshToken,
                        grantType: GrantType.REFRESH_TOKEN.name(),
                        scope: defaultScopes,
                        clientId: clientId,
                        clientSecret: clientSecret
                )
        ).then { AccessTokenResponse accessTokenResponse ->
            return buildResponse(accessTokenResponse, null)
        }.recover { Throwable ex ->
            if (appErrorUtils.isAppError(ex, ErrorCodes.OAuth.RefreshTokenExpired, ErrorCodes.OAuth.RefreshTokenInvalid)) {
                throw ex
            }
            appErrorUtils.throwUnknownError('getAuthToken', ex)
        }
    }

    @Override
    Promise<ConfirmEmailResponse> confirmEmail(ConfirmEmailRequest confirmEmailRequest) {
        requestValidator.validateRequiredApiHeaders().validateConfirmEmailRequest(confirmEmailRequest);

        return apiContextBuilder.buildApiContext().then { com.junbo.store.spec.model.ApiContext apiContext ->
            return resourceContainer.emailVerifyEndpoint.verifyEmail(confirmEmailRequest.evc, apiContext.locale.toString())
        }.recover { Throwable ex ->
            appErrorUtils.throwUnknownError('confirmEmail', ex)
        }.then { ViewModel viewModel ->
            if (viewModel != null) {
                if (viewModel.model?.verifyResult == Boolean.TRUE) {
                    return Promise.pure(new ConfirmEmailResponse(
                            isSuccess: true,
                            email: viewModel.model?.email as String
                    ))
                }

                return Promise.pure(new ConfirmEmailResponse(
                        isSuccess: false
                ))
            }
            return Promise.pure(new ConfirmEmailResponse(
                    isSuccess: false
            ))
        }
    }

    @Override
    Promise<com.junbo.store.spec.model.browse.document.Tos> getRegisterTos() {
        requestValidator.validateRequiredApiHeaders()

        return apiContextBuilder.buildApiContext().then { com.junbo.store.spec.model.ApiContext apiContext ->
            return resourceContainer.tosResource.list(new TosListOptions(title: tosCreateUser, countryId: apiContext.country.getId()))
        }.recover { Throwable ex ->
            appErrorUtils.throwUnknownError('getRegisterTos', ex)
        }.then { Results<Tos> tosResults ->
            if (tosResults == null || CollectionUtils.isEmpty(tosResults.items)) {
                return Promise.pure(null)
            }

            List<Tos> tosList = tosResults.items.sort { Tos tos ->
                return tos.version
            }

            Tos tos = tosList.reverse().find { Tos tos ->
                return tos.state == 'APPROVED'
            }
            if (tos == null) {
                return Promise.pure(null)
            }
            return Promise.pure(dataConverter.toStoreTos(tos, null))
        }
    }

    private Promise<AuthTokenResponse> innerSignIn(UserId userId, StoreUserEmail storeUserEmail) {
        return resourceContainer.tokenEndpoint.postToken(
                new AccessTokenRequest(
                        userId: IdFormatter.encodeId(userId),
                        clientId: clientId,
                        clientSecret: clientSecret,
                        grantType: GrantType.CLIENT_CREDENTIALS_WITH_USER_ID.name(),
                        scope: defaultScopes
                )
        ).then { AccessTokenResponse accessTokenResponse ->
            return buildResponse(accessTokenResponse, storeUserEmail)
        }
    }

    private Promise<AuthTokenResponse> innerSignIn(String email, String password) {
        return resourceContainer.tokenEndpoint.postToken( // todo : may need call credential verification first since post token does not return meaningful error when user credential is invalid
                new AccessTokenRequest(
                        username: email,
                        password: password,
                        clientId: clientId,
                        clientSecret: clientSecret,
                        grantType: GrantType.PASSWORD.name(),
                        scope: defaultScopes
                )
        ).then { AccessTokenResponse accessTokenResponse ->
            return buildResponse(accessTokenResponse, null);
        }
    }

    private Promise<AuthTokenResponse> buildResponse(AccessTokenResponse accessTokenResponse, StoreUserEmail storeUserEmail) {
        def response = fromAuthTokenResponse(accessTokenResponse)
        return resourceContainer.tokenInfoEndpoint.getTokenInfo(accessTokenResponse.accessToken).then { TokenInfo tokenInfo ->
            return resourceContainer.userResource.get(tokenInfo.sub, new UserGetOptions()).then { User user ->
                return resourceContainer.userPersonalInfoResource.get(user.username, new UserPersonalInfoGetOptions()).then { UserPersonalInfo usernamePersonalinfo ->
                    def userLoginName = ObjectMapperProvider.instance().treeToValue(usernamePersonalinfo.value, UserLoginName)
                    response.username = userLoginName.userName
                    response.userId = tokenInfo.sub
                    if (storeUserEmail != null) {
                        response.email = storeUserEmail
                        return Promise.pure(response)
                    }

                    UserPersonalInfoLink defaultEmail = null
                    if (!CollectionUtils.isEmpty(user.emails)) {
                        defaultEmail = user.emails.find {UserPersonalInfoLink link -> link.isDefault}
                    }
                    if (defaultEmail?.value == null) {
                        return Promise.pure(response)
                    }
                    return resourceContainer.userPersonalInfoResource.get(defaultEmail.value, new UserPersonalInfoGetOptions()).then { UserPersonalInfo emailInfo ->
                        response.email = new StoreUserEmail(isValidated: emailInfo.isValidated, value: ObjectMapperProvider.instance().treeToValue(emailInfo.value, Email).info)
                        return Promise.pure(response)
                    }
                }
            }
        }
    }

    private AuthTokenResponse fromAuthTokenResponse(AccessTokenResponse accessTokenResponse) {
        return new AuthTokenResponse(
                accessToken: accessTokenResponse.accessToken,
                refreshToken: accessTokenResponse.refreshToken,
                expiresIn: accessTokenResponse.expiresIn
        )
    }

    private Promise<Void> rollBackUser(User user) {
        String originalName = user.username
        user.username = null
        user.isAnonymous = true
        user.emails = []
        user.phones = []
        user.addresses = []

        return resourceContainer.userResource.put(user.getId(), user).syncThen {
            LOGGER.info('name=Rollback_Created_User, userId={}, name={}, rollback_name={}', IdFormatter.encodeId(user.getId()), originalName, user?.username)
        }
    }

    /**
     * Create User with basic info: username, password, pin
     *
     */
    private Promise createUserBasic(CreateUserRequest request, ApiContext apiContext, ErrorContext errorContext) {

        resourceContainer.userResource.create(new User(
                nickName: request.nickName,
                preferredLocale: new LocaleId(request.preferredLocale),
                countryOfResidence: new CountryId(request.cor),
        )).then { User u ->
                apiContext.user = u
                return Promise.pure()
        }.then {
            errorContext.fieldName = 'username'
            return resourceContainer.userPersonalInfoResource.create(
                    new UserPersonalInfo(
                            type: 'USERNAME',
                            userId: apiContext.user.getId(),
                            value: ObjectMapperProvider.instance().valueToTree(new UserLoginName(
                                    userName: request.username
                            ))
                    )
            ).then { UserPersonalInfo userPersonalInfo ->
                apiContext.user.username = userPersonalInfo.getId()
                apiContext.user.isAnonymous = false
                return resourceContainer.userResource.put(apiContext.user.getId(), apiContext.user).then { User u ->
                    apiContext.user = u
                    return Promise.pure()
                }
            }
        }.then {
            errorContext.fieldName = 'password'
            return resourceContainer.userCredentialResource.create(
                    apiContext.user.getId(),
                    new com.junbo.identity.spec.v1.model.UserCredential(type: 'PASSWORD', value: request.password)
            )
        }.then {
            if (request.pin == null) {
                return Promise.pure()
            }

            errorContext.fieldName = 'pin'
            return resourceContainer.userCredentialResource.create(
                    apiContext.user.getId(),
                    new com.junbo.identity.spec.v1.model.UserCredential(type: 'PIN', value: request.pin)
            )
        }
    }


    private Promise<StoreUserEmail> createUserPersonalInfo(CreateUserRequest createUserRequest, ApiContext apiContext, ErrorContext errorContext) {
        StoreUserEmail userEmail = new StoreUserEmail()
        errorContext.fieldName = 'email'
        return resourceContainer.userPersonalInfoResource.create(
                new UserPersonalInfo(
                        type: 'EMAIL',
                        userId: apiContext.user.getId(),
                        value: ObjectMapperProvider.instance().valueToTree(new Email(info: createUserRequest.email))
                )
        ).then { UserPersonalInfo emailInfo ->
            userEmail.isValidated = emailInfo.isValidated
            userEmail.value = ObjectMapperProvider.instance().treeToValue(emailInfo.value, Email)?.info
            apiContext.user.emails = [
                    new UserPersonalInfoLink(
                            isDefault: true,
                            value: emailInfo.getId()
                    )
            ]
            return Promise.pure()

        }.then { // create  dob
            if (createUserRequest.dob == null) {
                return Promise.pure()
            }

            errorContext.fieldName = 'dob'
            return resourceContainer.userPersonalInfoResource.create(
                    new UserPersonalInfo(
                            userId: apiContext.user.getId(),
                            type: 'DOB',
                            value: ObjectMapperProvider.instance().valueToTree(new UserDOB(info: createUserRequest.dob))
                    )
            ).then { UserPersonalInfo dobInfo ->
                apiContext.user.dob = dobInfo.getId()
                return Promise.pure()
            }

        }.then { // create name info
            if (createUserRequest.firstName == null
                    && createUserRequest.middleName == null
                    && createUserRequest.lastName == null) {
                return Promise.pure()
            }

            errorContext.fieldName = 'name'
            return resourceContainer.userPersonalInfoResource.create(
                    new UserPersonalInfo(
                            userId: apiContext.user.getId(),
                            type: 'NAME',
                            value: ObjectMapperProvider.instance().valueToTree(
                                    new UserName(
                                            givenName: createUserRequest.firstName,
                                            middleName: createUserRequest.middleName,
                                            familyName: createUserRequest.lastName
                                    )
                            )
                    )
            ).then { UserPersonalInfo nameInfo ->
                apiContext.user.name = nameInfo.getId()
                return Promise.pure()
            }

        }.then {
            return Promise.pure(userEmail)
        }
    }

    private Promise<AuthTokenResponse> processAfterSignIn(UserSignInRequest userSignInRequest, AuthTokenResponse authTokenResponse, com.junbo.store.spec.model.ApiContext apiContext) {
        CommonUtils.pushAuthHeader(authTokenResponse.accessToken)
        Promise.pure().then {
            return challengeHelper.checkTosChallenge(authTokenResponse.getUserId(), tosCreateUser, apiContext.country.getId(), userSignInRequest.challengeAnswer).then { Challenge challenge ->
                if (challenge == null) {
                    return Promise.pure(authTokenResponse)
                }

                AuthTokenResponse tosChallengeAuthToken = new AuthTokenResponse(
                        challenge: challenge
                )

                return Promise.pure(tosChallengeAuthToken)
            }.then { AuthTokenResponse response ->
                if (response.challenge != null) {
                    return Promise.pure(response)
                }

                initialItemPurchaseUtils.checkAndPurchaseInitialOffers(response.userId, false, apiContext).then {
                    return Promise.pure(response)
                }
            }
        }.recover { Throwable ex ->
            CommonUtils.popAuthHeader()
            throw ex
        }.then { AuthTokenResponse response ->
            CommonUtils.popAuthHeader()
            return Promise.pure(response)
        }
    }

    private Promise createUserCommunication(CreateUserRequest request, UserId userId, com.junbo.store.spec.model.ApiContext apc) {
        if (request.newsPromotionsAgreed == null || !request.newsPromotionsAgreed) {
            return Promise.pure(null)
        }
        return resourceContainer.communicationResource.list(new CommunicationListOptions(
                region: apc.country.getId()
        )).then { Results<Communication> communicationResults ->
            if (communicationResults == null || CollectionUtils.isEmpty(communicationResults.items)) {
                return Promise.pure(null)
            }

            Communication communication = communicationResults.items.find { Communication temp ->
                JsonNode jsonNode = temp?.locales?.get(COMMUNICATION_DEFAULT_LOCALE)
                if (jsonNode != null) {
                    CommunicationLocale communicationLocale = ObjectMapperProvider.instance().treeToValue(jsonNode, CommunicationLocale)
                    if (communicationLocale.name.equalsIgnoreCase(registerUserCommunication)) {
                        return true
                    }
                }

                return false
            }

            return resourceContainer.userCommunicationResource.create(new UserCommunication(
                    userId: userId,
                    communicationId: communication.getId()
            ))
        }
    }
}
