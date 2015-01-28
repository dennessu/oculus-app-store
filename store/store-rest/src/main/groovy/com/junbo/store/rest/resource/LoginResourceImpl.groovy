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
import com.junbo.identity.spec.v1.option.list.CountryListOptions
import com.junbo.identity.spec.v1.option.model.LocaleGetOptions
import com.junbo.identity.spec.v1.option.model.UserGetOptions
import com.junbo.identity.spec.v1.option.model.UserPersonalInfoGetOptions
import com.junbo.langur.core.promise.Promise
import com.junbo.oauth.spec.model.*
import com.junbo.store.clientproxy.FacadeContainer
import com.junbo.store.clientproxy.ResourceContainer
import com.junbo.store.clientproxy.error.AppErrorUtils
import com.junbo.store.clientproxy.error.ErrorCodes
import com.junbo.store.clientproxy.error.ErrorContext
import com.junbo.store.clientproxy.sentry.SentryFacade
import com.junbo.store.common.utils.CommonUtils
import com.junbo.store.rest.challenge.ChallengeHelper
import com.junbo.store.rest.utils.*
import com.junbo.store.spec.error.AppErrors
import com.junbo.store.spec.model.Challenge
import com.junbo.store.spec.model.ChallengeAnswer
import com.junbo.store.spec.model.external.sentry.SentryCategory
import com.junbo.store.spec.model.external.sentry.SentryFieldConstant
import com.junbo.store.spec.model.external.sentry.SentryResponse
import com.junbo.store.spec.model.identity.StoreUserEmail
import com.junbo.store.spec.model.login.*
import com.junbo.store.spec.resource.LoginResource
import groovy.transform.CompileStatic
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.util.Assert
import org.springframework.util.CollectionUtils

import javax.annotation.Resource
import javax.ws.rs.ext.Provider

@Provider
@CompileStatic
@Component('defaultLoginResource')
class LoginResourceImpl implements LoginResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginResourceImpl)

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

    @Resource(name = 'storeFacadeContainer')
    private FacadeContainer facadeContainer

    @Resource(name = 'storeRequestValidator')
    private RequestValidator requestValidator

    @Resource(name = 'storeAppErrorUtils')
    private AppErrorUtils appErrorUtils

    @Resource(name = 'storeContextBuilder')
    private ApiContextBuilder apiContextBuilder

    @Resource(name = 'storeSentryFacade')
    private SentryFacade sentryFacade

    @Value('${store.tos.createtostype}')
    private String tosCreateUserType

    @Value('${store.tos.privacytostype}')
    private String privacyPolicyTosType

    @Resource(name = 'storeDataConverter')
    DataConverter dataConverter

    @Resource(name = 'storeChallengeHelper')
    private ChallengeHelper challengeHelper

    @Resource(name = 'storeInitialItemPurchaseUtils')
    private InitialItemPurchaseUtils initialItemPurchaseUtils

    @Value('${store.communication.createuser}')
    private String registerUserCommunication

    @Value('${store.tos.freepurchase.enable}')
    private Boolean tosFreepurchaseEnable

    @Resource(name = 'storeIdentityUtils')
    private IdentityUtils identityUtils

    private class CreateUserContext {
        User user
        UserLoginName userLoginName
        StoreUserEmail storeUserEmail
    }

    @Override
    Promise<EmailCheckResponse> checkEmail(EmailCheckRequest emailCheckRequest) {
        requestValidator.validateRequiredApiHeaders().validateEmailCheckRequest(emailCheckRequest)
        EmailCheckResponse response
        ErrorContext errorContext = new ErrorContext()

        Promise.pure().then {
            def textMap = [:]
            sentryFacade.doSentryCheck(sentryFacade.createSentryRequest(SentryCategory.OCULUS_EMAIL_LOOKUP.value, textMap))
        }.recover { Throwable throwable ->
            LOGGER.error('checkEmail:  Call sentry error, Ignore', throwable)
            return Promise.pure()
        }.then { SentryResponse sentryResponse ->
            if (sentryResponse != null && sentryResponse.isBlockAccess()) {
                throw AppErrors.INSTANCE.sentryBlockCheckEmail().exception()
            }
            return Promise.pure()
        }.then {
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
            def textMap = [:]
            sentryFacade.doSentryCheck(sentryFacade.createSentryRequest(SentryCategory.OCULUS_EMAIL_LOOKUP.value, textMap))
        }.recover { Throwable throwable ->
            LOGGER.error('checkUsername:  Call sentry error, Ignore', throwable)
            return Promise.pure()
        }.then { SentryResponse sentryResponse ->
            if (sentryResponse != null && sentryResponse.isBlockAccess()) {
                throw AppErrors.INSTANCE.sentryBlockCheckUsername().exception()
            }
            return Promise.pure()
        }.then {
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
                return resourceContainer.userResource.checkUsername(userNameCheckRequest.username)
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
        CreateUserContext apiContext = new CreateUserContext()
        ErrorContext errorContext = new ErrorContext()
        AuthTokenResponse authTokenResponse

        return apiContextBuilder.buildApiContext().then { com.junbo.store.spec.model.ApiContext storeApiContext ->
            apc = storeApiContext
            return Promise.pure(null)
        }.then {
            requestValidator.validateAndGetCountry(new CountryId(request.cor))
        }.then { Country inputCountry ->
            return requestValidator.validateAndGetLocale(new LocaleId(request.preferredLocale)).recover { Throwable throwable ->
                LOGGER.error("CreateUser:  Call perferredlocale, Ignore")
                if (appErrorUtils.isAppError(throwable, ErrorCodes.Identity.LocaleNotFound)) {
                    request.preferredLocale = inputCountry.defaultLocale.toString()
                    return requestValidator.validateAndGetLocale(new LocaleId(request.preferredLocale))
                }

                throw throwable
            }
        }.then {
            requestValidator.validateTosExists(request.tosAgreed)
        }.then {
            def textMap = [:];
            textMap[SentryFieldConstant.EMAIL.value] = request.email
            textMap[SentryFieldConstant.USERNAME.value] = request.username
            textMap[SentryFieldConstant.REAL_NAME.value] = request.firstName + " " + request.lastName
            return Promise.pure().then {
                return sentryFacade.doSentryCheck(sentryFacade.createSentryRequest(SentryCategory.OCULUS_REGISTRATION_CREATE.value,
                        textMap))
            }.recover { Throwable throwable ->
                LOGGER.error("CreateUser:  Call sentry error, Ignore")
                return Promise.pure()
            }
        }.then { SentryResponse sentryResponse ->
            if (sentryResponse != null && sentryResponse.isBlockAccess()) {
                throw AppErrors.INSTANCE.sentryBlockRegisterAccess().exception()
            }
            return Promise.pure()
        }.then {
            createUserBasic(request, apiContext, errorContext).then {
                createUserPersonalInfo(request, apiContext, errorContext).then {
                    createPasswordAndPin(request, apiContext, errorContext)
                }
            }
        }.recover { Throwable ex ->
            appErrorUtils.throwOnFieldInvalidError(errorContext, ex, ErrorCodes.Identity.majorCode)
            if (appErrorUtils.isAppError(ex, ErrorCodes.Identity.CountryNotFound,
                    ErrorCodes.Identity.LocaleNotFound, ErrorCodes.Identity.InvalidPassword,
                    ErrorCodes.Identity.FieldDuplicate, ErrorCodes.Identity.AgeRestriction,
                    ErrorCodes.Sentry.BlockAccess, ErrorCodes.Identity.TosNotFound)) {
                throw ex
            }
            appErrorUtils.throwUnknownError('createUser', ex)
        }.then {
            // get the auth token
            innerSignIn(apiContext.user, apiContext.userLoginName, apiContext.storeUserEmail).then { AuthTokenResponse response ->
                authTokenResponse = response
                CommonUtils.pushAuthHeader(authTokenResponse.accessToken)
                Promise.parallel(
                    {
                        return resourceContainer.userTosAgreementResource.create(new UserTosAgreement(userId: apiContext.user.getId(),
                                tosId: request.tosAgreed,
                                agreementTime: new Date())).recover { Throwable ex ->
                            return Promise.pure(ex)
                        }
                    },
                    {
                        createUserCommunication(request, apiContext.user.getId(), apc).recover{ Throwable ex ->  // create user communication using the access token
                            return Promise.pure(ex)
                        }
                    },
                    {
                        initialItemPurchaseUtils.checkAndPurchaseInitialOffers(authTokenResponse.userId, true, apc).recover { Throwable ex ->
                            return Promise.pure(ex)
                        }
                    }
                ).recover { Throwable ex ->
                    CommonUtils.popAuthHeader()
                    throw ex
                }.then { List results ->
                    CommonUtils.popAuthHeader()
                    results.each { Object e ->
                        if (e instanceof Throwable) {
                            throw e
                        }
                    }
                    return Promise.pure()
                }
            }
        }.recover { Throwable ex ->
            if (apiContext.user?.getId() != null) {
                return rollBackUser(apiContext.user).then {
                    throw ex
                }
            }
            throw ex
        }.then {
            LOGGER.info("User apiContext.user.getId() created from horizon with client id $clientId")
            return facadeContainer.oAuthFacade.sendWelcomeEmail(request.preferredLocale, request.cor, apiContext.user.getId()).then {
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
            def textMap = [:];
            textMap[SentryFieldConstant.EMAIL.value] = userSignInRequest.email
            sentryFacade.doSentryCheck(sentryFacade.createSentryRequest(SentryCategory.OCULUS_LOGIN_MOBILE.value,
                    textMap))
        }.recover { Throwable throwable ->
            LOGGER.error("SignIn: Call sentry error, Ignore")
            return Promise.pure()
        }.then { SentryResponse sentryResponse ->
            if (sentryResponse != null && sentryResponse.isBlockAccess()) {
                throw AppErrors.INSTANCE.sentryBlockLoginAccess().exception()
            }
            return Promise.pure()
        }.then {
            innerSignIn(userSignInRequest.email, userSignInRequest.userCredential.value).recover { Throwable ex ->
                if (appErrorUtils.isAppError(ex, ErrorCodes.OAuth.InvalidCredential, ErrorCodes.Identity.MaximumLoginAttempt)) {
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
            return buildResponse(accessTokenResponse, null, null, null)
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
            return facadeContainer.oAuthFacade.verifyEmail(confirmEmailRequest.evc, apiContext.locale.getId().value)
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
        LocaleId localeId = null
        CountryId countryId = null
        requestValidator.validateRequiredApiHeaders()

        return apiContextBuilder.buildApiContext().then { com.junbo.store.spec.model.ApiContext apiContext ->
            localeId = apiContext.locale.getId()
            countryId = apiContext.country.getId()
            return identityUtils.lookupTos(tosCreateUserType, localeId, countryId).then { List<Tos> tosList ->
                if (CollectionUtils.isEmpty(tosList)) {
                    throw AppErrors.INSTANCE.RegisterTosNotFound().exception()
                }
                return Promise.pure(dataConverter.toStoreTos(identityUtils.selectTos(tosList, localeId), null, localeId));
            }
        }
    }

    @Override
    Promise<com.junbo.store.spec.model.browse.document.Tos> getPrivacyPolicy() {
        LocaleId localeId = null
        CountryId countryId = null

        return apiContextBuilder.buildApiContext().then { com.junbo.store.spec.model.ApiContext apiContext ->
            localeId = apiContext.locale.getId()
            countryId = apiContext.country.getId()
            return identityUtils.lookupTos(privacyPolicyTosType, localeId, countryId).then { List<Tos> tosList ->
                if (CollectionUtils.isEmpty(tosList)) {
                    throw AppErrors.INSTANCE.tosNotFound().exception()
                }
                return Promise.pure(dataConverter.toStoreTos(identityUtils.selectTos(tosList, localeId), null, localeId));
            }
        }
    }

    @Override
    Promise<GetSupportedCountriesResponse> getSupportedCountries() {
         requestValidator.validateRequiredApiHeaders()

        List<String> supportedCountries = new ArrayList();
        return resourceContainer.countryResource.list(new CountryListOptions(
                properties: 'countryCode'
        )).then { Results<Country> countryResults ->
            GetSupportedCountriesResponse response = new GetSupportedCountriesResponse(
                    supportedCountries: []
            )
            countryResults.items.each { Country country ->
                if (!StringUtils.isEmpty(country.countryCode)) {
                    response.supportedCountries.add(country.countryCode)
                }
            }

            response.supportedCountries = response.supportedCountries.unique().sort()
            return Promise.pure(response)
        }
    }

    private Tos getSupportedTos(List<Tos> tosList, LocaleId localeId) {
        if (org.apache.commons.collections.CollectionUtils.isEmpty(tosList) || localeId == null) {
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
            for (Tos tos : tosList) {
                if (!CollectionUtils.isEmpty(tos.locales)) {
                    if (tos.locales.any { LocaleId tosLocaleId ->
                        return tosLocaleId == current
                    }) {
                        return tos
                    }
                }
            }

            fallback = locale.fallbackLocale
            if (current == fallback || fallback == null) {
                break
            }
            current = fallback
        }

        return null
    }


    private Promise<AuthTokenResponse> innerSignIn(User createdUser, UserLoginName userLoginName, StoreUserEmail storeUserEmail) {
        return resourceContainer.tokenEndpoint.postToken(
                new AccessTokenRequest(
                        userId: IdFormatter.encodeId(createdUser.getId()),
                        clientId: clientId,
                        clientSecret: clientSecret,
                        grantType: GrantType.CLIENT_CREDENTIALS_WITH_USER_ID.name(),
                        scope: defaultScopes
                )
        ).then { AccessTokenResponse accessTokenResponse ->
            return buildResponse(accessTokenResponse, createdUser, userLoginName, storeUserEmail)
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
            return buildResponse(accessTokenResponse, null, null, null);
        }
    }

    private Promise<AuthTokenResponse> buildResponse(AccessTokenResponse accessTokenResponse, User createdUser, UserLoginName userLoginName, StoreUserEmail storeUserEmail) {
        def response = fromAuthTokenResponse(accessTokenResponse)
        if (createdUser != null) {
            Assert.notNull(userLoginName)
            Assert.notNull(storeUserEmail)
            response.username = userLoginName.userName
            response.userId = createdUser.getId()
            response.email = storeUserEmail
            return Promise.pure(response)
        }
        return resourceContainer.tokenInfoEndpoint.getTokenInfo(accessTokenResponse.accessToken).then { TokenInfo tokenInfo -> // todo use expand ?
            return resourceContainer.userResource.get(tokenInfo.sub, new UserGetOptions()).then { User user ->
                return resourceContainer.userPersonalInfoResource.get(user.username, new UserPersonalInfoGetOptions()).then { UserPersonalInfo usernamePersonalinfo ->
                    userLoginName = ObjectMapperProvider.instanceNotStrict().treeToValue(usernamePersonalinfo.value, UserLoginName)
                    response.username = userLoginName.userName
                    response.userId = tokenInfo.sub

                    UserPersonalInfoLink defaultEmail = null
                    if (!CollectionUtils.isEmpty(user.emails)) {
                        defaultEmail = user.emails.find {UserPersonalInfoLink link -> link.isDefault}
                    }
                    if (defaultEmail?.value == null) {
                        return Promise.pure(response)
                    }
                    return resourceContainer.userPersonalInfoResource.get(defaultEmail.value, new UserPersonalInfoGetOptions()).then { UserPersonalInfo emailInfo ->
                        response.email = new StoreUserEmail(isValidated: emailInfo.isValidated, value: ObjectMapperProvider.instanceNotStrict().treeToValue(emailInfo.value, Email).info)
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
    private Promise createUserBasic(CreateUserRequest request, CreateUserContext apiContext, ErrorContext errorContext) {
        resourceContainer.userResource.create(new User(
                nickName: request.nickName,
                preferredLocale: new LocaleId(request.preferredLocale),
                countryOfResidence: new CountryId(request.cor),
        )).then { User u ->
                apiContext.user = u
                return Promise.pure()
        }
    }

    private Promise createPasswordAndPin(CreateUserRequest createUserRequest, CreateUserContext createUserContext, ErrorContext errorContext) {
        errorContext.fieldName = 'password'
        return resourceContainer.userCredentialResource.create(
                createUserContext.user.getId(),
                new com.junbo.identity.spec.v1.model.UserCredential(type: 'PASSWORD', value: createUserRequest.password)
        ).then {
            if (createUserRequest.pin == null) {
                return Promise.pure()
            }
            errorContext.fieldName = 'pin'
            return resourceContainer.userCredentialResource.create(
                    createUserContext.user.getId(),
                    new com.junbo.identity.spec.v1.model.UserCredential(type: 'PIN', value: createUserRequest.pin)
            )
        }
    }

    private Promise createUserPersonalInfo(CreateUserRequest createUserRequest, CreateUserContext apiContext, ErrorContext errorContext) {
        final String[] fieldList = ["username", "email", "dob", "name"]
        Promise.parallel(
            // create user name
            {
                return resourceContainer.userPersonalInfoResource.create(
                        new UserPersonalInfo(
                                type: 'USERNAME',
                                userId: apiContext.user.getId(),
                                value: ObjectMapperProvider.instanceNotStrict().valueToTree(new UserLoginName(
                                        userName: createUserRequest.username
                                ))
                        )
                ).then { UserPersonalInfo userPersonalInfo ->
                    apiContext.user.username = userPersonalInfo.getId()
                    apiContext.user.isAnonymous = false
                    apiContext.userLoginName = ObjectMapperProvider.instanceNotStrict().treeToValue(userPersonalInfo.value, UserLoginName)
                    return Promise.pure()
                }.recover { Throwable throwable ->
                    return Promise.pure(throwable)
                }
            },

            // create email
            {
                StoreUserEmail userEmail = new StoreUserEmail()
                errorContext.fieldName = 'email'
                return resourceContainer.userPersonalInfoResource.create(
                        new UserPersonalInfo(
                                type: 'EMAIL',
                                userId: apiContext.user.getId(),
                                value: ObjectMapperProvider.instanceNotStrict().valueToTree(new Email(info: createUserRequest.email))
                        )
                ).then { UserPersonalInfo emailInfo ->
                    userEmail.isValidated = emailInfo.isValidated
                    userEmail.value = ObjectMapperProvider.instanceNotStrict().treeToValue(emailInfo.value, Email)?.info
                    apiContext.storeUserEmail = userEmail
                    apiContext.user.emails = [
                            new UserPersonalInfoLink(
                                    isDefault: true,
                                    value: emailInfo.getId()
                            )
                    ]
                    return Promise.pure()
                }.recover { Throwable throwable ->
                    return Promise.pure(throwable)
                }
            },

            // create dob
            {
                if (createUserRequest.dob == null) {
                    return Promise.pure()
                }

                errorContext.fieldName = 'dob'
                return resourceContainer.userPersonalInfoResource.create(
                        new UserPersonalInfo(
                                userId: apiContext.user.getId(),
                                type: 'DOB',
                                value: ObjectMapperProvider.instanceNotStrict().valueToTree(new UserDOB(info: createUserRequest.dob))
                        )
                ).then { UserPersonalInfo dobInfo ->
                    apiContext.user.dob = dobInfo.getId()
                    return Promise.pure()
                }.recover { Throwable throwable ->
                    return Promise.pure(throwable)
                }
            },

            // create name
            {
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
                                value: ObjectMapperProvider.instanceNotStrict().valueToTree(
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
                }.recover { Throwable throwable ->
                    return Promise.pure(throwable)
                }
            }
        ).then { List results ->
            Assert.isTrue(fieldList.length == results.size())
            for (int i = 0;i < fieldList.length;++i) {
                if (results[i] instanceof Throwable) {
                    errorContext.fieldName = fieldList[i]
                    throw results[i]
                }
            }

            // update user
            return resourceContainer.userResource.put(apiContext.user.getId(), apiContext.user).then { User u ->
                    apiContext.user = u
                    return Promise.pure()
            }
        }
    }

    private Promise<AuthTokenResponse> processAfterSignIn(UserSignInRequest userSignInRequest, AuthTokenResponse authTokenResponse, com.junbo.store.spec.model.ApiContext apiContext) {
        CommonUtils.pushAuthHeader(authTokenResponse.accessToken)
        Promise.pure().then {
            return checkTosChallengeForLogin(authTokenResponse.getUserId(), apiContext.country.getId(), userSignInRequest.challengeAnswer, authTokenResponse, apiContext.locale.getId())
                    .then { AuthTokenResponse response ->
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

    private Promise<AuthTokenResponse> checkTosChallengeForLogin(UserId userId, CountryId country, ChallengeAnswer challengeAnswer, AuthTokenResponse authTokenResponse, LocaleId localeId) {
        if (tosFreepurchaseEnable) {
            return challengeHelper.checkTosChallenge(userId, tosCreateUserType, country, challengeAnswer, localeId).then { Challenge challenge ->
                if (challenge == null) {
                    return Promise.pure(authTokenResponse)
                }

                AuthTokenResponse tosChallengeAuthToken = new AuthTokenResponse(
                        challenge: challenge
                )

                return Promise.pure(tosChallengeAuthToken)
            }
        } else {
            return Promise.pure(authTokenResponse)
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
                    CommunicationLocale communicationLocale = ObjectMapperProvider.instanceNotStrict().treeToValue(jsonNode, CommunicationLocale)
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
