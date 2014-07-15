package com.junbo.store.rest.resource

import com.junbo.common.enumid.CountryId
import com.junbo.common.enumid.LocaleId
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.common.model.Results
import com.junbo.identity.spec.v1.model.Email
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.model.UserCredential
import com.junbo.identity.spec.v1.model.UserCredentialVerifyAttempt
import com.junbo.identity.spec.v1.model.UserDOB
import com.junbo.identity.spec.v1.model.UserName
import com.junbo.identity.spec.v1.model.UserPersonalInfo
import com.junbo.identity.spec.v1.model.UserPersonalInfoLink
import com.junbo.identity.spec.v1.option.list.UserListOptions
import com.junbo.identity.spec.v1.option.model.UserGetOptions
import com.junbo.oauth.spec.model.AccessTokenRequest
import com.junbo.oauth.spec.model.AccessTokenResponse
import com.junbo.oauth.spec.model.TokenInfo
import com.junbo.store.rest.utils.ResourceContainer
import com.junbo.store.spec.model.ResponseStatus
import com.junbo.store.spec.model.login.AuthTokenRequest
import com.junbo.store.spec.model.login.AuthTokenResponse
import com.junbo.store.spec.model.login.CreateUserRequest
import com.junbo.store.spec.model.login.UserCredentialCheckRequest
import com.junbo.store.spec.model.login.UserCredentialCheckResponse
import com.junbo.store.spec.model.login.UserCredentialRateRequest
import com.junbo.store.spec.model.login.UserCredentialRateResponse
import com.junbo.store.spec.model.login.UserNameCheckRequest
import com.junbo.store.spec.model.login.UserNameCheckResponse
import com.junbo.store.spec.model.login.UserSignInRequest
import com.junbo.store.spec.resource.LoginResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.annotation.Resource
import javax.ws.rs.ext.Provider

@Provider
@Scope('prototype')
@CompileStatic
@Component('defaultLoginResource')
class LoginResourceImpl implements  LoginResource {

    String clientId = "client"

    String clientSecret = "secret"

    @Resource(name = 'storeResourceContainer')
    private ResourceContainer resourceContainer

    @Override
    Promise<UserNameCheckResponse> checkUserName(UserNameCheckRequest userNameCheckRequest) {
        return resourceContainer.userResource.list(
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
    Promise<AuthTokenResponse> createUser(CreateUserRequest createUserRequest) {
        User user = new User(
                username: createUserRequest.username,
                preferredLocale: new LocaleId(createUserRequest.preferredLocale),
                countryOfResidence: new CountryId(createUserRequest.cor),
        )

        User createdUser = null

        // create user and credential
        Promise promise = resourceContainer.userResource.create(user).then { User u ->
            createdUser = u
            resourceContainer.userCredentialResource.create(
                    createdUser.getId(),
                    new UserCredential(
                            type: 'PASSWORD',
                            value: createUserRequest.password
                    )
            ).then {
                resourceContainer.userCredentialResource.create(
                        createdUser.getId(),
                        new UserCredential(
                                type: 'PIN',
                                value: createUserRequest.pinCode
                        )
                )
            }
        }

        // set user email
        promise = promise.then {
            resourceContainer.userPersonalInfoResource.create(
                    new UserPersonalInfo(
                            type: 'EMAIL',
                            userId: createdUser.getId(),
                            value:  ObjectMapperProvider.instance().valueToTree(new Email(info: createUserRequest.emailAddress))
                    )
            ).then { UserPersonalInfo emailInfo ->
                createdUser.emails = [
                        new UserPersonalInfoLink(
                                isDefault: true,
                                value: emailInfo.getId()
                        )
                ]
                resourceContainer.userResource.put(createdUser.getId(), createdUser).then { User u ->
                    createdUser = u
                    return Promise.pure(createdUser)
                }
            }
        }

        // set user dob
        promise = promise.then {
            resourceContainer.userPersonalInfoResource.create(
                    new UserPersonalInfo(
                            userId: createdUser.getId(),
                            type: 'DOB',
                            value:  ObjectMapperProvider.instance().valueToTree(new UserDOB(info: createUserRequest.dob))
                    )
            ).then { UserPersonalInfo dobInfo ->
                createdUser.dob = new UserPersonalInfoLink(isDefault: true, value: dobInfo.getId())
                resourceContainer.userResource.put(createdUser.getId(), createdUser).then { User u ->
                    createdUser = u
                    return Promise.pure(createdUser)
                }
            }
        }

        // set user name
        promise = promise.then {
            resourceContainer.userPersonalInfoResource.create(
                    new UserPersonalInfo(
                            userId: createdUser.getId(),
                            type: 'NAME',
                            value:  ObjectMapperProvider.instance().valueToTree(
                                    new UserName(
                                            givenName: createUserRequest.firstName,
                                            middleName: createUserRequest.middleName,
                                            familyName: createUserRequest.lastName,
                                            nickName: createUserRequest.nickName
                                    )
                            )
                    )
            ).then { UserPersonalInfo nameInfo ->
                createdUser.name = new UserPersonalInfoLink(isDefault: true, value: nameInfo.getId())
                resourceContainer.userResource.put(createdUser.getId(), createdUser).then { User u ->
                    createdUser = u
                    return Promise.pure(createdUser)
                }
            }
        }

        // todo set the tos and newsPromotionsAgreed
        // get the auth token
        return promise.then {
            innerSignIn(createdUser.username, createUserRequest.password)
        }
    }

    @Override
    Promise<AuthTokenResponse> signIn(UserSignInRequest userSignInRequest) {
        if (!'PASSWORD'.equalsIgnoreCase(userSignInRequest.userCredential.type)) { //
            // todo error
        }
        return innerSignIn(userSignInRequest.username, userSignInRequest.userCredential.value)
    }

    @Override
    Promise<UserCredentialCheckResponse> checkUserCredential(UserCredentialCheckRequest userCredentialCheckRequest) {
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

    @Override
    Promise<AuthTokenResponse> getAuthToken(AuthTokenRequest tokenRequest) {
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
                    response.status = ResponseStatus.SUCCESS
                    return Promise.pure(response)
                }
            }
        }
    }

    private Promise<AuthTokenResponse>  innerSignIn(String username, String password) {
        return resourceContainer.tokenEndpoint.postToken(
                    new AccessTokenRequest(
                            username: username,
                            password: password,
                            clientId: clientId,
                            clientSecret: clientSecret,
                            grantType: 'password',
                            scope: 'offline'
                    )
            ).then { AccessTokenResponse accessTokenResponse ->
                def response = fromAuthTokenResponse(accessTokenResponse)
                response.username = username
                response.status = ResponseStatus.SUCCESS
                return Promise.pure(response)
            }
    }


    private AuthTokenResponse fromAuthTokenResponse(AccessTokenResponse accessTokenResponse) {
        return new AuthTokenResponse(
                accessToken: accessTokenResponse.accessToken,
                refreshToken: accessTokenResponse.refreshToken,
                expiresIn: accessTokenResponse.expiresIn
        )
    }
}
