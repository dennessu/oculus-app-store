package com.junbo.identity.rest.resource.v1

import com.junbo.common.id.Id
import com.junbo.common.id.UserCommunicationId
import com.junbo.common.model.Results
import com.junbo.identity.core.service.Created201Marker
import com.junbo.identity.core.service.filter.UserCommunicationFilter
import com.junbo.identity.core.service.validator.UserCommunicationValidator
import com.junbo.identity.data.repository.UserCommunicationRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.UserCommunication
import com.junbo.identity.spec.v1.option.list.UserOptinListOptions
import com.junbo.identity.spec.v1.option.model.UserOptinGetOptions
import com.junbo.identity.spec.v1.resource.UserCommunicationResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Created by liangfu on 4/11/14.
 */
@Transactional
@CompileStatic
class UserCommunicationResourceImpl implements UserCommunicationResource {

    @Autowired
    private UserCommunicationRepository userCommunicationRepository

    @Autowired
    private Created201Marker created201Marker

    @Autowired
    private UserCommunicationFilter userCommunicationFilter

    @Autowired
    private UserCommunicationValidator userCommunicationValidator

    @Override
    Promise<UserCommunication> create(UserCommunication userCommunication) {
        if (userCommunication == null) {
            throw new IllegalArgumentException('userCommunication is null')
        }

        userCommunication = userCommunicationFilter.filterForCreate(userCommunication)

        return userCommunicationValidator.validateForCreate(userCommunication).then {
            return userCommunicationRepository.create(userCommunication).then {
                UserCommunication newUserCommunication ->
                created201Marker.mark((Id)newUserCommunication.id)

                newUserCommunication = userCommunicationFilter.filterForGet(newUserCommunication, null)
                return Promise.pure(newUserCommunication)
            }
        }
    }

    @Override
    Promise<UserCommunication> get(UserCommunicationId userCommunicationId, UserOptinGetOptions getOptions) {
        if (getOptions == null) {
            throw new IllegalArgumentException('getOptions is null')
        }

        return userCommunicationValidator.validateForGet(userCommunicationId).then {
            UserCommunication userCommunication ->
            userCommunication = userCommunicationFilter.filterForGet(userCommunication,
                    getOptions.properties?.split(',') as List<String>)

            return Promise.pure(userCommunication)
        }
    }

    @Override
    Promise<UserCommunication> patch(UserCommunicationId userCommunicationId, UserCommunication userCommunication) {
        if (userCommunicationId == null) {
            throw new IllegalArgumentException('userCommunicationId is null')
        }

        if (userCommunication == null) {
            throw new IllegalArgumentException('userCommunication is null')
        }

        return userCommunicationRepository.get(userCommunicationId).then { UserCommunication oldUserOptin ->
            if (oldUserOptin == null) {
                throw AppErrors.INSTANCE.userOptinNotFound(userCommunicationId).exception()
            }

            userCommunication = userCommunicationFilter.filterForPatch(userCommunication, oldUserOptin)

            return userCommunicationValidator.validateForUpdate(userCommunicationId, userCommunication, oldUserOptin).
                    then {

                return userCommunicationRepository.update(userCommunication).then {
                    UserCommunication newUserCommunication ->
                    newUserCommunication = userCommunicationFilter.filterForGet(newUserCommunication, null)
                    return Promise.pure(newUserCommunication)
                }
            }
        }
    }

    @Override
    Promise<UserCommunication> put(UserCommunicationId userCommunicationId, UserCommunication userCommunication) {
        if (userCommunicationId == null) {
            throw new IllegalArgumentException('userCommunicationId is null')
        }

        if (userCommunication == null) {
            throw new IllegalArgumentException('userCommunication is null')
        }

        return userCommunicationRepository.get(userCommunicationId).then { UserCommunication oldUserOptin ->
            if (oldUserOptin == null) {
                throw AppErrors.INSTANCE.userOptinNotFound(userCommunicationId).exception()
            }

            userCommunication = userCommunicationFilter.filterForPut(userCommunication, oldUserOptin)

            return userCommunicationValidator.validateForUpdate(userCommunicationId, userCommunication, oldUserOptin)
                    .then {
                userCommunicationRepository.update(userCommunication).then { UserCommunication newUserCommunication ->
                    newUserCommunication = userCommunicationFilter.filterForGet(newUserCommunication, null)
                    return Promise.pure(newUserCommunication)
                }
            }
        }
    }

    @Override
    Promise<Void> delete(UserCommunicationId userCommunicationId) {
        return userCommunicationValidator.validateForGet(userCommunicationId).then {
            return userCommunicationRepository.delete(userCommunicationId)
        }
    }

    @Override
    Promise<Results<UserCommunication>> list(UserOptinListOptions listOptions) {
        return userCommunicationValidator.validateForSearch(listOptions).then {
            return userCommunicationRepository.search(listOptions).then { List<UserCommunication> userCommunications ->
                def result = new Results<UserCommunication>(items: [])

                userCommunications.each { UserCommunication newUserCommunication ->
                    if (newUserCommunication != null) {
                        newUserCommunication = userCommunicationFilter.filterForGet(newUserCommunication,
                                listOptions.properties?.split(',') as List<String>)
                    }

                    if (newUserCommunication != null) {
                        result.items.add(newUserCommunication)
                    }
                }

                return Promise.pure(result)
            }
        }
    }
}
