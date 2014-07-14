package com.junbo.identity.rest.resource.v1

import com.junbo.authorization.AuthorizeContext
import com.junbo.authorization.AuthorizeService
import com.junbo.authorization.RightsScope
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.UserId
import com.junbo.common.model.Results
import com.junbo.common.rs.Created201Marker
import com.junbo.identity.auth.UserPropertyAuthorizeCallbackFactory
import com.junbo.identity.core.service.filter.UserCredentialFilter
import com.junbo.identity.core.service.validator.UserCredentialValidator
import com.junbo.identity.data.identifiable.CredentialType
import com.junbo.identity.data.mapper.ModelMapper
import com.junbo.identity.data.repository.UserPasswordRepository
import com.junbo.identity.data.repository.UserPinRepository
import com.junbo.identity.spec.model.users.UserPassword
import com.junbo.identity.spec.model.users.UserPin
import com.junbo.identity.spec.v1.model.UserCredential
import com.junbo.identity.spec.v1.option.list.UserCredentialListOptions
import com.junbo.identity.spec.v1.resource.UserCredentialResource
import com.junbo.langur.core.promise.Promise
import com.junbo.oom.core.MappingContext
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Created by liangfu on 4/9/14.
 */
@Transactional
@CompileStatic
class UserCredentialResourceImpl implements UserCredentialResource {

    @Autowired
    private UserPasswordRepository userPasswordRepository

    @Autowired
    private UserPinRepository userPinRepository

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
            throw new IllegalArgumentException('userId is null')
        }

        if (userCredential == null) {
            throw new IllegalArgumentException('userCredential is null')
        }

        def callback = authorizeCallbackFactory.create(userCredential.userId)
        return RightsScope.with(authorizeService.authorize(callback)) {
            if (!AuthorizeContext.hasRights('create')) {
                throw AppCommonErrors.INSTANCE.forbidden().exception()
            }

            userCredential = userCredentialFilter.filterForCreate(userCredential)

            return userCredentialValidator.validateForCreate(userId, userCredential).then { Object obj ->
                if (obj == null) {
                    throw new IllegalArgumentException('userCredential mapping exception')
                }
                if (obj instanceof UserPassword) {
                    return userPasswordRepository.searchByUserIdAndActiveStatus(userId, true, Integer.MAX_VALUE, 0).then {
                        List<UserPassword> passwordList ->
                            return Promise.each(passwordList) { UserPassword userPassword ->
                                userPassword.active = false
                                return userPasswordRepository.update(userPassword)
                            }
                    }.then {
                        return userPasswordRepository.create((UserPassword) obj).then { UserPassword userPassword ->
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
                    return userPinRepository.searchByUserIdAndActiveStatus(userId, true, Integer.MAX_VALUE, 0).then {
                        List<UserPin> pinList ->
                            return Promise.each(pinList) { UserPin userPin ->
                                userPin.active = false
                                return userPinRepository.update(userPin)
                            }
                    }.then {
                        return userPinRepository.create((UserPin) obj).then { UserPin userPin ->
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
                    return userPasswordRepository.searchByUserIdAndActiveStatus(listOptions.userId, listOptions.active,
                            listOptions.limit, listOptions.offset).then { List<UserPassword> userPasswordList ->
                        return wrappUserCredential(userPasswordList, resultList, listOptions)
                    }
                }
                else {
                    return userPasswordRepository.searchByUserId(listOptions.userId, listOptions.limit,
                            listOptions.offset).then { List<UserPassword> userPasswordList ->
                        return wrappUserCredential(userPasswordList, resultList, listOptions)
                    }
                }
            } else if (listOptions.type == CredentialType.PIN.toString()) {
                return userPinRepository.searchByUserId(listOptions.userId, listOptions.limit,
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
}
