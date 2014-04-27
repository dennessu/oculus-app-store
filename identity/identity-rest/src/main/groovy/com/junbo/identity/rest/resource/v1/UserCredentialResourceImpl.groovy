package com.junbo.identity.rest.resource.v1

import com.junbo.common.id.Id
import com.junbo.common.id.UserId
import com.junbo.common.model.Results
import com.junbo.identity.core.service.Created201Marker
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
import com.junbo.identity.spec.v1.option.list.UserPasswordListOptions
import com.junbo.identity.spec.v1.option.list.UserPinListOptions
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
    private Created201Marker created201Marker

    @Autowired
    private ModelMapper modelMapper

    @Autowired
    private UserCredentialFilter userCredentialFilter

    @Autowired
    private UserCredentialValidator userCredentialValidator

    @Override
    Promise<UserCredential> create(UserId userId, UserCredential userCredential) {
        if (userId == null) {
            throw new IllegalArgumentException('userId is null')
        }

        if (userCredential == null) {
            throw new IllegalArgumentException('userCredential is null')
        }
        userCredential = userCredentialFilter.filterForCreate(userCredential)

        return userCredentialValidator.validateForCreate(userId, userCredential).then { Object obj ->
            if (obj == null) {
                throw new IllegalArgumentException('userCredential mapping exception')
            }
            if (obj instanceof UserPassword) {
                userPasswordRepository.search(new UserPasswordListOptions(
                        userId: userId,
                        active: true
                )).then { List<UserPassword> passwordList ->
                    passwordList.each { UserPassword userPassword ->
                        userPassword.active = false
                        userPasswordRepository.update(userPassword)
                    }
                }

                return userPasswordRepository.create((UserPassword)obj).then { UserPassword userPassword ->
                    if (userPassword == null) {
                        throw new RuntimeException()
                    }

                    UserCredential newUserCredential =
                            modelMapper.passwordToCredential(userPassword, new MappingContext())
                    newUserCredential.type = CredentialType.PASSWORD.toString()
                    created201Marker.mark((Id) newUserCredential.id)

                    newUserCredential = userCredentialFilter.filterForGet(newUserCredential, null)
                    return Promise.pure(newUserCredential)
                }
            } else if (obj instanceof UserPin) {
                userPinRepository.search(new UserPinListOptions(
                        userId: userId,
                        active: true
                )).then { List<UserPin> pinList ->
                    pinList.each { UserPin userPin ->
                        userPin.active = false
                        userPinRepository.update(userPin)
                    }
                }

                userPinRepository.create((UserPin)obj).then { UserPin userPin ->
                    if (userPin == null) {
                        throw new RuntimeException()
                    }

                    UserCredential newUserCredential = modelMapper.pinToCredential(userPin, new MappingContext())
                    newUserCredential.type = CredentialType.PIN.toString()
                    created201Marker.mark((Id) newUserCredential.id)

                    newUserCredential = userCredentialFilter.filterForGet(newUserCredential, null)
                    return Promise.pure(newUserCredential)
                }
            } else {
                throw new RuntimeException()
            }
        }
    }

    @Override
    Promise<Results<UserCredential>> list(UserId userId, UserCredentialListOptions listOptions) {

        def resultList = new Results<UserCredential>(items: [])
        return userCredentialValidator.validateForSearch(userId, listOptions).then {
            if (listOptions.type == 'password') {
                UserPasswordListOptions options = new UserPasswordListOptions()
                options.setUserId(listOptions.userId)
                userPasswordRepository.search(options).then { List<UserPassword> userPasswordList ->
                    if (userPasswordList == null) {
                        return Promise.pure(resultList)
                    }

                    userPasswordList.each { UserPassword userPassword ->

                        UserCredential newUserCredential =
                                modelMapper.passwordToCredential(userPassword, new MappingContext())
                        if (newUserCredential != null) {
                            newUserCredential = userCredentialFilter.filterForGet(newUserCredential,
                                    listOptions.properties?.split(',') as List<String>)

                            resultList.items.add(newUserCredential)
                        }
                    }
                    return Promise.pure(resultList)
                }
            } else if (listOptions.type == 'pin') {
                UserPinListOptions options = new UserPinListOptions()
                options.setUserId(listOptions.userId)
                userPinRepository.search(options).then { List<UserPin> userPinList ->
                    if (userPinList == null) {
                        return Promise.pure(userPinList)
                    }

                    userPinList.each { UserPin userPin ->
                        UserCredential newUserCredential = modelMapper.pinToCredential(userPin, new MappingContext())
                        if (newUserCredential != null) {
                            newUserCredential = userCredentialFilter.filterForGet(newUserCredential,
                                    listOptions.properties?.split(',') as List<String>)

                            resultList.items.add(newUserCredential)
                        }
                    }
                    return Promise.pure(resultList)
                }
            } else {
                throw new RuntimeException()
            }
        }
    }
}
