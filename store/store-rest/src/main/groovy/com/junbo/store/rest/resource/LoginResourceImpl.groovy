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
import com.junbo.identity.spec.v1.model.*
import com.junbo.identity.spec.v1.option.list.UserListOptions
import com.junbo.identity.spec.v1.option.model.UserGetOptions
import com.junbo.identity.spec.v1.option.model.UserPersonalInfoGetOptions
import com.junbo.langur.core.promise.Promise
import com.junbo.oauth.spec.model.AccessTokenRequest
import com.junbo.oauth.spec.model.AccessTokenResponse
import com.junbo.oauth.spec.model.TokenInfo
import com.junbo.store.rest.utils.RequestValidator
import com.junbo.store.rest.utils.ResourceContainer
import com.junbo.store.spec.error.AppErrors
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
class LoginResourceImpl implements  LoginResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginResourceImpl)

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
        Promise.pure(null).then {
            requestValidator.validateUserNameCheckRequest(userNameCheckRequest)
        }.then {
            resourceContainer.userResource.list(
                    new UserListOptions(username : userNameCheckRequest.username)
            ).then { Results<User> results ->
                UserNameCheckResponse response = new UserNameCheckResponse()
                if (results.items.isEmpty()) {
                    response.status = ResponseStatus.SUCCESS.name()
                } else {
                    response.status = ResponseStatus.FAIL.name()
                    response.statusDetail = ResponseStatus.Detail.USERNAME_ALREADY_USED.name();
                }
                return Promise.pure(response)
            }
        }
    }

    @Override
    Promise<UserCredentialRateResponse> rateUserCredential(UserCredentialRateRequest userCredentialRateRequest) { // todo implement this method
        return Promise.pure(
                new UserCredentialRateResponse(
                        status: ResponseStatus.SUCCESS.name(),
                        strength: "STRONG"
                )
        )
    }

    @Override
    Promise<UserCredentialChangeResponse> changeUserCredential(UserCredentialChangeRequest userCredentialChangeRequest) {
        Promise.pure(null).then {
            requestValidator.validateUserCredentialChangeRequest(userCredentialChangeRequest)
        }.then {
            resourceContainer.userCredentialVerifyAttemptResource.create(
                    new UserCredentialVerifyAttempt(
                            username: userCredentialChangeRequest.username,
                            type: userCredentialChangeRequest.oldCredential.type,
                            value: userCredentialChangeRequest.oldCredential.value
                    )
            )
        }.then { UserCredentialVerifyAttempt attempt ->
            resourceContainer.userCredentialResource.create(attempt.userId, new com.junbo.identity.spec.v1.model.UserCredential(
                    userId: attempt.userId,
                    type: userCredentialChangeRequest.newCredential.type,
                    value: userCredentialChangeRequest.newCredential.value
            ))
        }.recover { Throwable ex ->
            handleError(null, ex)
        }.syncThen {
            return new UserCredentialChangeResponse(status: ResponseStatus.SUCCESS.name())
        }
    }

    @Override
    Promise<AuthTokenResponse> createUser(CreateUserRequest createUserRequest) {
        User user
        String fieldName = null
        Promise.pure(null).then { // create user
            requestValidator.validateCreateUserRequest(createUserRequest).then {
                user = new User(
                        preferredLocale: new LocaleId(createUserRequest.preferredLocale),
                        countryOfResidence: new CountryId(createUserRequest.cor),
                        nickName: createUserRequest.nickName
                )
                return resourceContainer.userResource.create(user).then { User u ->
                    user = u
                    return resourceContainer.userPersonalInfoResource.create(
                            new UserPersonalInfo(
                                    type: 'USERNAME',
                                    userId: user.getId(),
                                    value: ObjectMapperProvider.instance().valueToTree(new UserLoginName(
                                            userName: createUserRequest.username
                                    ))
                            )
                    )
                }.then { UserPersonalInfo userPersonalInfo ->
                    user.username = userPersonalInfo.getId()
                    user.isAnonymous = false
                    return resourceContainer.userResource.put(user.getId(), user)
                }.then {
                    return resourceContainer.userCredentialResource.create(
                            user.getId(),
                            new com.junbo.identity.spec.v1.model.UserCredential(type: 'PASSWORD', value: createUserRequest.password)
                    )
                }.then {
                    return resourceContainer.userCredentialResource.create(
                            user.getId(),
                            new com.junbo.identity.spec.v1.model.UserCredential(type: 'PIN', value: createUserRequest.pinCode)
                    )
                }
            }
        }.then { // create email info
            fieldName = 'email'
            resourceContainer.userPersonalInfoResource.create(
                    new UserPersonalInfo(
                            type: 'EMAIL',
                            userId: user.getId(),
                            value:  ObjectMapperProvider.instance().valueToTree(new Email(info: createUserRequest.email))
                    )
            ).then { UserPersonalInfo emailInfo ->
                user.emails = [
                        new UserPersonalInfoLink(
                                isDefault: true,
                                value: emailInfo.getId()
                        )
                ]
                resourceContainer.userResource.put(user.getId(), user).then { User u ->
                    user = u
                    return Promise.pure(user)
                }
            }
        }.then { // create dob info
            fieldName = 'dob'
            resourceContainer.userPersonalInfoResource.create(
                    new UserPersonalInfo(
                            userId: user.getId(),
                            type: 'DOB',
                            value:  ObjectMapperProvider.instance().valueToTree(new UserDOB(info: createUserRequest.dob))
                    )
            ).then { UserPersonalInfo dobInfo ->
                user.dob = dobInfo.getId()
                resourceContainer.userResource.put(user.getId(), user).then { User u ->
                    user = u
                    return Promise.pure(user)
                }
            }
        }.then { // create name info
            fieldName = 'name'
            resourceContainer.userPersonalInfoResource.create(
                    new UserPersonalInfo(
                            userId: user.getId(),
                            type: 'NAME',
                            value:  ObjectMapperProvider.instance().valueToTree(
                                    new UserName(
                                            givenName: createUserRequest.firstName,
                                            middleName: createUserRequest.middleName,
                                            familyName: createUserRequest.lastName
                                    )
                            )
                    )
            ).then { UserPersonalInfo nameInfo ->
                user.name = nameInfo.getId()
                resourceContainer.userResource.put(user.getId(), user).then { User u ->
                    user = u
                    return Promise.pure(user)
                }
            }
            // todo set the tos and newsPromotionsAgreed
        }.recover { Throwable ex ->
            Promise.pure(null).then {
                if (user?.getId() != null) {
                    return rollBackUser(user)
                }
                return Promise.pure(null)
            }.then {
                handleError(fieldName, ex)
            }
        }.then {
            resourceContainer.emailVerifyEndpoint.sendVerifyEmail(createUserRequest.preferredLocale, createUserRequest.cor, user.getId())
        }.then {
            // get the auth token
            innerSignIn(user.username, createUserRequest.password)
        }
    }

    @Override
    Promise<AuthTokenResponse> signIn(UserSignInRequest userSignInRequest) {
        Promise.pure(null).then {
            requestValidator.validateUserSignInRequest(userSignInRequest)
        }.then {
            return innerSignIn(userSignInRequest.username, userSignInRequest.userCredential.value)}
    }

    @Override
    Promise<UserCredentialCheckResponse> checkUserCredential(UserCredentialCheckRequest userCredentialCheckRequest) {
        Promise.pure(null).then {
            requestValidator.validateUserCredentialCheckRequest(userCredentialCheckRequest)
        }.then {
            resourceContainer.userCredentialVerifyAttemptResource.create(
                    new UserCredentialVerifyAttempt(
                            username: userCredentialCheckRequest.username,
                            type: userCredentialCheckRequest.userCredential.type,
                            value: userCredentialCheckRequest.userCredential.value
                    )
            ).then { UserCredentialVerifyAttempt attempt ->
                UserCredentialCheckResponse response = new UserCredentialCheckResponse()
                if (attempt.succeeded) {
                    response.status = ResponseStatus.SUCCESS.name()
                } else {
                    response.status = ResponseStatus.FAIL.name()
                    response.statusDetail = ResponseStatus.Detail.USER_CREDENTIAL_INVALID
                }
                return Promise.pure(response)
            }
        }
    }

    @Override
    Promise<AuthTokenResponse> getAuthToken(AuthTokenRequest tokenRequest) {
        Promise.pure(null).then {
            requestValidator.validateAuthTokenRequest(tokenRequest)
        }.then {
            return resourceContainer.tokenEndpoint.postToken(
                    new AccessTokenRequest(
                            refreshToken: tokenRequest.refreshToken,
                            grantType: 'refresh_token',
                            scope: 'offline',
                            clientId: clientId,
                            clientSecret: clientSecret
                    )
            ).then { AccessTokenResponse accessTokenResponse ->
                def response = fromAuthTokenResponse(accessTokenResponse)
                resourceContainer.tokenInfoEndpoint.getTokenInfo(accessTokenResponse.accessToken).then { TokenInfo tokenInfo ->
                    resourceContainer.userResource.get(tokenInfo.sub, new UserGetOptions(properties : 'username')).then { User user ->
                        response.username = user.username
                        response.userId = tokenInfo.sub
                        response.status = ResponseStatus.SUCCESS
                        return Promise.pure(response)
                    }
                }
            }
        }
    }

    private Promise<AuthTokenResponse> innerSignIn(String username, String password) {
        return resourceContainer.userResource.list(new UserListOptions(
                username: username
        )).then { Results<User> userResults ->
            if (userResults.items == null || userResults.items.size() != 1) {
                throw AppErrors.INSTANCE.userNotFoundByUsername().exception()
            }

            return innerSignIn(userResults.items.get(0).username, password)
        }
    }

    private Promise<AuthTokenResponse>  innerSignIn(UserPersonalInfoId usernameId, String password) {
        return getUsername(usernameId.value).then { String username ->
            return resourceContainer.tokenEndpoint.postToken( // todo : may need call credential verification first since post token does not return meaningful error when user credential is invalid
                    new AccessTokenRequest(
                            username: username,
                            password: password,
                            clientId: clientId,
                            clientSecret: clientSecret,
                            grantType: 'password',
                            scope: 'offline'
                    )
            ).then { AccessTokenResponse accessTokenResponse ->
                resourceContainer.tokenInfoEndpoint.getTokenInfo(accessTokenResponse.accessToken).then { TokenInfo tokenInfo ->
                    def response = fromAuthTokenResponse(accessTokenResponse)
                    response.username = username
                    response.userId = tokenInfo.sub
                    response.status = ResponseStatus.SUCCESS
                    return Promise.pure(response)
                }
            }
        }
    }

    Promise<String> getUsername(Long usernameId) {
        if (usernameId == null) {
            return Promise.pure(null)
        }

        return resourceContainer.userPersonalInfoResource.get(new UserPersonalInfoId(usernameId), new UserPersonalInfoGetOptions())
                .then { UserPersonalInfo info ->
            if (info == null || !info.type.equalsIgnoreCase('USERNAME')) {
                return Promise.pure(null)
            }

            try {
                UserLoginName userLoginName = ObjectMapperProvider.instance().treeToValue(info.value, UserLoginName)
                return Promise.pure(userLoginName.userName)
            } catch (Exception ex) {
                return Promise.pure(null)
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
        user.username = null // remove the username field
        user.isAnonymous = true // set the user to be anonymous
        user.emails = []
        user.phones = []
        user.addresses = []
        resourceContainer.userResource.put(user.getId(), user).syncThen {
            LOGGER.info('name=Rollback_Created_User, userId={}, name={}, rollback_name={}', IdFormatter.encodeId(user.getId()), originalName, user?.username)
        }
    }

    private void handleError(String field, Throwable ex) {
        if (!(ex instanceof AppErrorException)) {
            throw ex
        }
        AppError appError = ((AppErrorException) ex).error
        String []codes = appError.error().code.split('\\.')
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
