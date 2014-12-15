package com.junbo.identity.rest.resource.v1

import com.junbo.authorization.AuthorizeContext
import com.junbo.authorization.AuthorizeService
import com.junbo.authorization.RightsScope
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.error.AppErrorException
import com.junbo.common.id.UserId
import com.junbo.common.model.Results
import com.junbo.common.rs.Created201Marker
import com.junbo.identity.auth.UserPropertyAuthorizeCallbackFactory
import com.junbo.identity.core.service.filter.UserCredentialFilter
import com.junbo.identity.core.service.util.CipherHelper
import com.junbo.identity.core.service.validator.UserCredentialValidator
import com.junbo.identity.data.identifiable.CredentialType
import com.junbo.identity.data.mapper.ModelMapper
import com.junbo.identity.service.UserPasswordService
import com.junbo.identity.service.UserPinService
import com.junbo.identity.spec.model.users.UserPassword
import com.junbo.identity.spec.model.users.UserPin
import com.junbo.identity.spec.v1.model.RateUserCredentialRequest
import com.junbo.identity.spec.v1.model.RateUserCredentialResponse
import com.junbo.identity.spec.v1.model.UserCredential
import com.junbo.identity.spec.v1.option.list.UserCredentialListOptions
import com.junbo.identity.spec.v1.resource.UserCredentialResource
import com.junbo.langur.core.promise.Promise
import com.junbo.oom.core.MappingContext
import groovy.transform.CompileStatic
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Created by liangfu on 4/9/14.
 */
@Transactional
@CompileStatic
class UserCredentialResourceImpl implements UserCredentialResource {

    @Autowired
    private UserPasswordService userPasswordService

    @Autowired
    private UserPinService userPinService

    @Autowired
    private ModelMapper modelMapper

    @Autowired
    private UserCredentialFilter userCredentialFilter

    @Autowired
    private UserCredentialValidator userCredentialValidator

    @Autowired
    private AuthorizeService authorizeService

    @Autowired
    private UserPropertyAuthorizeCallbackFactory authorizeCallbackFactory

    @Override
    Promise<UserCredential> create(UserId userId, UserCredential userCredential) {
        if (userId == null) {
            throw AppCommonErrors.INSTANCE.parameterRequired('userId').exception()
        }

        if (userCredential == null) {
            throw AppCommonErrors.INSTANCE.requestBodyRequired().exception()
        }

        def callback = authorizeCallbackFactory.create(userId)
        return RightsScope.with(authorizeService.authorize(callback)) {
            if (!AuthorizeContext.hasRights('create')) {
                throw AppCommonErrors.INSTANCE.forbidden().exception()
            }

            if (StringUtils.isEmpty(userCredential.currentPassword) && !AuthorizeContext.hasRights('admin')) {
                throw AppCommonErrors.INSTANCE.forbidden().exception()
            }

            userCredential = userCredentialFilter.filterForCreate(userCredential)

            return userCredentialValidator.validateForCreate(userId, userCredential).then { Object obj ->
                if (obj == null) {
                    throw new IllegalArgumentException('userCredential mapping exception')
                }
                if (obj instanceof UserPassword) {
                    return userPasswordService.searchByUserIdAndActiveStatus(userId, true, Integer.MAX_VALUE, 0).then {
                        List<UserPassword> passwordList ->
                            return Promise.each(passwordList) { UserPassword userPassword ->
                                userPassword.active = false
                                return userPasswordService.update(userPassword, userPassword)
                            }
                    }.then {
                        return userPasswordService.create((UserPassword) obj).then { UserPassword userPassword ->
                            if (userPassword == null) {
                                throw new RuntimeException('Create Password exception')
                            }

                            UserCredential newUserCredential =
                                    modelMapper.passwordToCredential(userPassword, new MappingContext())
                            newUserCredential.type = CredentialType.PASSWORD.toString()
                            Created201Marker.mark(newUserCredential.getId())

                            newUserCredential = userCredentialFilter.filterForGet(newUserCredential, null)
                            return Promise.pure(newUserCredential)
                        }
                    }
                } else if (obj instanceof UserPin) {
                    return userPinService.searchByUserIdAndActiveStatus(userId, true, Integer.MAX_VALUE, 0).then {
                        List<UserPin> pinList ->
                            return Promise.each(pinList) { UserPin userPin ->
                                userPin.active = false
                                return userPinService.update(userPin, userPin)
                            }
                    }.then {
                        return userPinService.create((UserPin) obj).then { UserPin userPin ->
                            if (userPin == null) {
                                throw new RuntimeException()
                            }

                            UserCredential newUserCredential = modelMapper.pinToCredential(userPin, new MappingContext())
                            newUserCredential.type = CredentialType.PIN.toString()
                            Created201Marker.mark(newUserCredential.getId())

                            newUserCredential = userCredentialFilter.filterForGet(newUserCredential, null)
                            return Promise.pure(newUserCredential)
                        }
                    }
                } else {
                    throw new RuntimeException()
                }
            }
        }
    }

    @Override
    Promise<Results<UserCredential>> list(UserId userId, UserCredentialListOptions listOptions) {

        def resultList = new Results<UserCredential>(items: [])
        return userCredentialValidator.validateForSearch(userId, listOptions).then {
            if (listOptions.type == CredentialType.PASSWORD.toString()) {
                if (listOptions.active != null) {
                    return userPasswordService.searchByUserIdAndActiveStatus(listOptions.userId, listOptions.active,
                            listOptions.limit, listOptions.offset).then { List<UserPassword> userPasswordList ->
                        return wrappUserCredential(userPasswordList, resultList, listOptions)
                    }
                }
                else {
                    return userPasswordService.searchByUserId(listOptions.userId, listOptions.limit,
                            listOptions.offset).then { List<UserPassword> userPasswordList ->
                        return wrappUserCredential(userPasswordList, resultList, listOptions)
                    }
                }
            } else if (listOptions.type == CredentialType.PIN.toString()) {
                return userPinService.searchByUserId(listOptions.userId, listOptions.limit,
                        listOptions.offset).then { List<UserPin> userPinList ->
                    if (userPinList == null) {
                        return Promise.pure(userPinList)
                    }

                    userPinList.each { UserPin userPin ->
                        UserCredential newUserCredential = modelMapper.pinToCredential(userPin, new MappingContext())
                        if (newUserCredential != null) {
                            newUserCredential.type = CredentialType.PIN.toString()
                            newUserCredential = userCredentialFilter.filterForGet(newUserCredential,
                                    listOptions.properties?.split(',') as List<String>)

                            resultList.items.add(newUserCredential)
                        }
                    }
                    return Promise.pure(resultList)
                }
            } else {
                throw AppCommonErrors.INSTANCE.parameterInvalid("credentialType").exception()
            }
        }
    }

    private Promise<Results<UserCredential>> wrappUserCredential(List<UserPassword> userPasswordList, Results<UserCredential> resultList, UserCredentialListOptions listOptions) {
        if (userPasswordList == null) {
            return Promise.pure(resultList)
        }

        return Promise.each(userPasswordList) { UserPassword userPassword ->
            def callback = authorizeCallbackFactory.create(userPassword.userId)
            return RightsScope.with(authorizeService.authorize(callback)) {
                UserCredential newUserCredential =
                        modelMapper.passwordToCredential(userPassword, new MappingContext())
                if (newUserCredential != null && AuthorizeContext.hasRights('read')) {
                    newUserCredential.type = CredentialType.PASSWORD.toString()
                    newUserCredential = userCredentialFilter.filterForGet(newUserCredential,
                            listOptions.properties?.split(',') as List<String>)

                    resultList.items.add(newUserCredential)
                    return Promise.pure(newUserCredential)
                } else {
                    return Promise.pure(null)
                }
            }
        }.then {
            return Promise.pure(resultList)
        }
    }

    @Override
    Promise<RateUserCredentialResponse> rateCredential(RateUserCredentialRequest request) {
        if (request == null) {
            throw AppCommonErrors.INSTANCE.requestBodyRequired().exception()
        }

        if (request.type == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('type').exception()
        }

        if (request.value == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('value').exception()
        }

        def response = new RateUserCredentialResponse()
        if (request.type == 'PASSWORD') {
            try {
                CipherHelper.validatePassword(request.value)
            } catch(AppErrorException ignore) {
                response.strength = 'INVALID'
            }

            if (response.strength == null && !StringUtils.isEmpty(request.context?.username)) {
                if (request.value.toLowerCase().contains(request.context.username.toLowerCase())) {
                    response.strength = 'INVALID'
                }
            }

            if (response.strength == null) {
                response.strength = CipherHelper.calcPwdStrength(request.value)
            }
        } else {
            throw AppCommonErrors.INSTANCE.fieldInvalidEnum('type', 'PASSWORD').exception()
        }

        return Promise.pure(response)
    }
}
