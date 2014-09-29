package com.junbo.store.rest.resource

import com.junbo.common.enumid.CountryId
import com.junbo.common.enumid.LocaleId
import com.junbo.common.id.UserId
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.common.util.IdFormatter
import com.junbo.identity.spec.v1.model.*
import com.junbo.identity.spec.v1.option.model.UserGetOptions
import com.junbo.identity.spec.v1.option.model.UserPersonalInfoGetOptions
import com.junbo.langur.core.promise.Promise
import com.junbo.oauth.spec.model.*
import com.junbo.store.clientproxy.ResourceContainer
import com.junbo.store.clientproxy.error.AppErrorUtils
import com.junbo.store.clientproxy.error.ErrorCodes
import com.junbo.store.clientproxy.error.ErrorContext
import com.junbo.store.clientproxy.sentry.SentryFacade
import com.junbo.store.rest.utils.ApiContextBuilder
import com.junbo.store.rest.utils.RequestValidator
import com.junbo.store.spec.model.identity.StoreUserEmail
import com.junbo.store.spec.model.login.*
import com.junbo.store.spec.resource.LoginResource
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.util.CollectionUtils
import org.springframework.util.StringUtils

import javax.annotation.Resource
import javax.ws.rs.core.Response
import javax.ws.rs.ext.Provider

@Provider
@CompileStatic
@Component('defaultLoginResource')
class LoginResourceImpl implements LoginResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginResourceImpl)

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

    private class ApiContext {
        User user
    }

    @Override
    Promise<UserNameCheckResponse> checkUserName(UserNameCheckRequest userNameCheckRequest) {
        requestValidator.validateRequiredApiHeaders().validateUserNameCheckRequest(userNameCheckRequest)
        UserNameCheckResponse response
        ErrorContext errorContext = new ErrorContext()

        Promise.pure().then {
            if (!StringUtils.isEmpty(userNameCheckRequest.email)) {
                errorContext.fieldName = 'email'
                return resourceContainer.userResource.checkEmail(userNameCheckRequest.email)
            }
            errorContext.fieldName = 'username'
            return resourceContainer.userResource.checkUsername(userNameCheckRequest.username)
        }.recover { Throwable ex ->
            if (appErrorUtils.isAppError(ex, ErrorCodes.Identity.FieldDuplicate)) {
                response = new UserNameCheckResponse(isAvailable : false)
                return Promise.pure()
            }
            appErrorUtils.throwOnFieldInvalidError(errorContext, ex)
            appErrorUtils.throwUnknownError('checkUserName', ex)
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
        requestValidator.validateRequiredApiHeaders().validateCreateUserRequest(request)
        ApiContext apiContext = new ApiContext()
        ErrorContext errorContext = new ErrorContext()
        StoreUserEmail storeUserEmail

        requestValidator.validateAndGetCountry(new CountryId(request.cor)).then {
            requestValidator.validateAndGetLocale(new LocaleId(request.preferredLocale))
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
            // todo set the tos and newsPromotionsAgreed
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
                appErrorUtils.throwOnFieldInvalidError(errorContext, ex)
                if (appErrorUtils.isAppError(ex, ErrorCodes.Identity.CountryNotFound,
                        ErrorCodes.Identity.LocaleNotFound, ErrorCodes.Identity.InvalidPassword,
                        ErrorCodes.Identity.FieldDuplicate, ErrorCodes.Identity.AgeRestriction)) {
                    throw ex
                }
                appErrorUtils.throwUnknownError('createUser', ex)
            }
        }.then {
            return resourceContainer.emailVerifyEndpoint.sendWelcomeEmail(request.preferredLocale, request.cor, apiContext.user.getId())
        }.then {
            // get the auth token
            innerSignIn(apiContext.user.getId(), storeUserEmail)
        }
    }

    @Override
    Promise<AuthTokenResponse> signIn(UserSignInRequest userSignInRequest) {
        requestValidator.validateRequiredApiHeaders().validateUserSignInRequest(userSignInRequest)

        return innerSignIn(userSignInRequest.email, userSignInRequest.userCredential.value).recover { Throwable ex ->
            if (appErrorUtils.isAppError(ex, ErrorCodes.OAuth.InvalidCredential)) {
                throw ex
            }
            appErrorUtils.throwUnknownError('signIn', ex)
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
        }.then { Response response ->
            if (response != null && response.entity instanceof ViewModel) {
                ViewModel viewModel = (ViewModel)response.entity
                if (viewModel.model?.verifyResult == Boolean.TRUE) {
                    return Promise.pure(new ConfirmEmailResponse(
                            isSuccess: true
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

}
