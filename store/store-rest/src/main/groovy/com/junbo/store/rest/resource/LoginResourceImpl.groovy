package com.junbo.store.rest.resource

import com.junbo.common.enumid.CountryId
import com.junbo.common.enumid.LocaleId
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.error.AppError
import com.junbo.common.error.AppErrorException
import com.junbo.common.error.ErrorDetail
import com.junbo.common.id.UserPersonalInfoId
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.common.model.Results
import com.junbo.common.util.IdFormatter
import com.junbo.identity.spec.v1.model.Email
import com.junbo.identity.spec.v1.model.RateUserCredentialContext
import com.junbo.identity.spec.v1.model.RateUserCredentialRequest
import com.junbo.identity.spec.v1.model.RateUserCredentialResponse
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.model.UserCredentialVerifyAttempt
import com.junbo.identity.spec.v1.model.UserDOB
import com.junbo.identity.spec.v1.model.UserName
import com.junbo.identity.spec.v1.model.UserLoginName
import com.junbo.identity.spec.v1.model.UserPersonalInfo
import com.junbo.identity.spec.v1.model.UserPersonalInfoLink
import com.junbo.identity.spec.v1.option.list.UserListOptions
import com.junbo.identity.spec.v1.option.model.UserGetOptions
import com.junbo.identity.spec.v1.option.model.UserPersonalInfoGetOptions
import com.junbo.langur.core.promise.Promise
import com.junbo.oauth.spec.model.AccessTokenRequest
import com.junbo.oauth.spec.model.AccessTokenResponse
import com.junbo.oauth.spec.model.TokenInfo
import com.junbo.store.rest.utils.RequestValidator
import com.junbo.store.rest.utils.ResourceContainer
import com.junbo.store.spec.model.ResponseStatus
import com.junbo.store.spec.model.login.*
import com.junbo.store.spec.resource.LoginResource
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

import javax.annotation.Resource
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

    @Resource(name = 'storeResourceContainer')
    private ResourceContainer resourceContainer

    @Resource(name = 'storeRequestValidator')
    private RequestValidator requestValidator

    @Override
    Promise<UserNameCheckResponse> checkUserName(UserNameCheckRequest userNameCheckRequest) {
        requestValidator.validateUserNameCheckRequest(userNameCheckRequest)

        return resourceContainer.userCredentialVerifyAttemptResource.create(new UserCredentialVerifyAttempt(
                username: userNameCheckRequest.username,
                type: "CHECK_NAME",
                value: ""
        )).recover { Throwable ex ->
            if (ex instanceof AppErrorException) {
                def appError = ((AppErrorException) ex).error.error()
                if (appError.code == '131.108') { // usernameNotFound
                    return Promise.pure(new UserCredentialVerifyAttempt(
                            succeeded: false
                    ))
                } else if (appError.code == '131.107') { // userInInvalidStatus
                    return Promise.pure(new UserCredentialVerifyAttempt(
                            succeeded: true
                    ))
                }
            }

            throw ex
        }.then { UserCredentialVerifyAttempt attempt ->
            return Promise.pure(new UserNameCheckResponse(
                    isAvailable: !attempt.succeeded
            ))
        }
    }

    @Override
    Promise<UserCredentialRateResponse> rateUserCredential(UserCredentialRateRequest request) {
        requestValidator.validateUserCredentialRateRequest(request)

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
    Promise<AuthTokenResponse> createUser(CreateUserRequest createUserRequest) {
        requestValidator.validateCreateUserRequest(createUserRequest)

        User user
        user = new User(
                nickName: createUserRequest.nickName,
                preferredLocale: new LocaleId(createUserRequest.preferredLocale),
                countryOfResidence: new CountryId(createUserRequest.cor),
        )

        String fieldName = null
        resourceContainer.userResource.create(user).then { User u ->
                user = u
                return Promise.pure()
        }.then {
            fieldName = 'username'
            return resourceContainer.userPersonalInfoResource.create(
                        new UserPersonalInfo(
                            type: 'USERNAME',
                            userId: user.getId(),
                            value: ObjectMapperProvider.instance().valueToTree(new UserLoginName(
                                userName: createUserRequest.username
                            ))
                          )
                         ).then { UserPersonalInfo userPersonalInfo ->
                     user.username = userPersonalInfo.getId()
                     user.isAnonymous = false
                     return resourceContainer.userResource.put(user.getId(), user).then { User u ->
                        user = u
                        return Promise.pure()
                    }
            }
        }.then {
            fieldName = 'password'
            return resourceContainer.userCredentialResource.create(
                    user.getId(),
                    new com.junbo.identity.spec.v1.model.UserCredential(type: 'PASSWORD', value: createUserRequest.password)
            )
        }.then {
            if (createUserRequest.pin == null) {
                return Promise.pure()
            }

            fieldName = 'pin'
            return resourceContainer.userCredentialResource.create(
                    user.getId(),
                    new com.junbo.identity.spec.v1.model.UserCredential(type: 'PIN', value: createUserRequest.pin)
            )
        }.then { // create email info
            fieldName = 'email'

            return resourceContainer.userPersonalInfoResource.create(
                    new UserPersonalInfo(
                            type: 'EMAIL',
                            userId: user.getId(),
                            value: ObjectMapperProvider.instance().valueToTree(new Email(info: createUserRequest.email))
                    )
            ).then { UserPersonalInfo emailInfo ->
                user.emails = [
                        new UserPersonalInfoLink(
                                isDefault: true,
                                value: emailInfo.getId()
                        )
                ]

                return Promise.pure()
            }
        }.then { // create   info
            if (createUserRequest.dob == null) {
                return Promise.pure()
            }

            fieldName = 'dob'
            return resourceContainer.userPersonalInfoResource.create(
                    new UserPersonalInfo(
                            userId: user.getId(),
                            type: 'DOB',
                            value: ObjectMapperProvider.instance().valueToTree(new UserDOB(info: createUserRequest.dob))
                    )
            ).then { UserPersonalInfo dobInfo ->
                user.dob = dobInfo.getId()  
                return Promise.pure()
            }
        }.then { // create name info
            if (createUserRequest.firstName == null
                    && createUserRequest.middleName == null
                    && createUserRequest.lastName == null) {
                return Promise.pure()
            }

            fieldName = 'name'
            return resourceContainer.userPersonalInfoResource.create(
                    new UserPersonalInfo(
                            userId: user.getId(),
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
                user.name = nameInfo.getId()
                return Promise.pure()
            }

            // todo set the tos and newsPromotionsAgreed
        }.then {
            return resourceContainer.userResource.put(user.getId(), user).then { User u ->
                user = u
                return Promise.pure()
            }
        }.recover { Throwable ex ->
            def promise = Promise.pure()
            
            if (user?.getId() != null) {
                promise = rollBackUser(user)
            }

            promise.then {
                handleError(fieldName, ex)
                throw new RuntimeException("Shouldn't be here.")
            }
        }.then {
            return resourceContainer.emailVerifyEndpoint.sendVerifyEmail(createUserRequest.preferredLocale, createUserRequest.cor, user.getId())
        }.then {
            // get the auth token
            innerSignIn(createUserRequest.username, createUserRequest.password)
        }
    }

    @Override
    Promise<AuthTokenResponse> signIn(UserSignInRequest userSignInRequest) {
        requestValidator.validateUserSignInRequest(userSignInRequest)

        return innerSignIn(userSignInRequest.username, userSignInRequest.userCredential.value)
    }

    @Override
    Promise<AuthTokenResponse> getAuthToken(AuthTokenRequest tokenRequest) {
        requestValidator.validateAuthTokenRequest(tokenRequest)

        return resourceContainer.tokenEndpoint.postToken(
                new AccessTokenRequest(
                        refreshToken: tokenRequest.refreshToken,
                        grantType: 'refresh_token',
                        scope: 'offline storeapi entitlement',
                        clientId: clientId,
                        clientSecret: clientSecret
                )
        ).then { AccessTokenResponse accessTokenResponse ->
            def response = fromAuthTokenResponse(accessTokenResponse)
            resourceContainer.tokenInfoEndpoint.getTokenInfo(accessTokenResponse.accessToken).then { TokenInfo tokenInfo ->
                resourceContainer.userResource.get(tokenInfo.sub, new UserGetOptions()).then { User user ->
                    resourceContainer.userPersonalInfoResource.get(user.username, new UserPersonalInfoGetOptions()).then { UserPersonalInfo usernamePersonalinfo ->
                        def userLoginName = ObjectMapperProvider.instance().treeToValue(usernamePersonalinfo.value, UserLoginName)

                        response.username = userLoginName.userName
                        response.userId = tokenInfo.sub
                        return Promise.pure(response)
                    }
                }
            }
        }
    }

    private Promise<AuthTokenResponse> innerSignIn(String username, String password) {
        return resourceContainer.tokenEndpoint.postToken( // todo : may need call credential verification first since post token does not return meaningful error when user credential is invalid
                new AccessTokenRequest(
                        username: username,
                        password: password,
                        clientId: clientId,
                        clientSecret: clientSecret,
                        grantType: 'password',
                        scope: 'offline storeapi entitlement'
                )
        ).then { AccessTokenResponse accessTokenResponse ->
            def response = fromAuthTokenResponse(accessTokenResponse)
            resourceContainer.tokenInfoEndpoint.getTokenInfo(accessTokenResponse.accessToken).then { TokenInfo tokenInfo ->
                resourceContainer.userResource.get(tokenInfo.sub, new UserGetOptions()).then { User user ->
                    resourceContainer.userPersonalInfoResource.get(user.username, new UserPersonalInfoGetOptions()).then { UserPersonalInfo usernamePersonalinfo ->
                        def userLoginName = ObjectMapperProvider.instance().treeToValue(usernamePersonalinfo.value, UserLoginName)

                        response.username = userLoginName.userName
                        response.userId = tokenInfo.sub
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

    private void handleError(String field, Throwable ex) {
        if (!(ex instanceof AppErrorException)) {
            throw ex
        }

        AppError appError = ((AppErrorException) ex).error

        String[] codes = appError.error().code.split('\\.')

        if (codes.length == 2) {
            if (codes[1] == '001') {
                LOGGER.error('name=Invalid_Field_Error', ex)

                AppError resultAppError
                if (field != null) {
                    resultAppError = AppCommonErrors.INSTANCE.fieldInvalid(
                            appError.error().details.collect {ErrorDetail errorDetail -> return new ErrorDetail(field, errorDetail.reason)}.toArray(new ErrorDetail[0])
                    )
                } else {
                    resultAppError = AppCommonErrors.INSTANCE.fieldInvalid(appError.error().details.toArray(new ErrorDetail[0]))
                }
                throw resultAppError.exception()
            }
        } else {
            LOGGER.error('name=Invalid_Error_Code_Format, code={}', appError.error().code)
        }

        throw ex
    }
}
