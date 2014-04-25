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
    private UserCommunicationFilter userOptinFilter

    @Autowired
    private UserCommunicationValidator userOptinValidator

    @Override
    Promise<UserCommunication> create(UserCommunication userOptin) {
        if (userOptin == null) {
            throw new IllegalArgumentException('userOptin is null')
        }

        userOptin = userOptinFilter.filterForCreate(userOptin)

        userOptinValidator.validateForCreate(userOptin).then {
            userCommunicationRepository.create(userOptin).then { UserCommunication newUserOptin ->
                created201Marker.mark((Id)newUserOptin.id)

                newUserOptin = userOptinFilter.filterForGet(newUserOptin, null)
                return Promise.pure(newUserOptin)
            }
        }
    }

    @Override
    Promise<UserCommunication> get(UserCommunicationId userOptinId, UserOptinGetOptions getOptions) {
        if (getOptions == null) {
            throw new IllegalArgumentException('getOptions is null')
        }

        userOptinValidator.validateForGet(userOptinId).then { UserCommunication newUserOptin ->
            newUserOptin = userOptinFilter.filterForGet(newUserOptin,
                    getOptions.properties?.split(',') as List<String>)

            return Promise.pure(newUserOptin)
        }
    }

    @Override
    Promise<UserCommunication> patch(UserCommunicationId userOptinId, UserCommunication userOptin) {
        if (userOptinId == null) {
            throw new IllegalArgumentException('userOptinId is null')
        }

        if (userOptin == null) {
            throw new IllegalArgumentException('userOptin is null')
        }

        return userCommunicationRepository.get(userOptinId).then { UserCommunication oldUserOptin ->
            if (oldUserOptin == null) {
                throw AppErrors.INSTANCE.userOptinNotFound(userOptinId).exception()
            }

            userOptin = userOptinFilter.filterForPatch(userOptin, oldUserOptin)

            userOptinValidator.validateForUpdate(userOptinId, userOptin, oldUserOptin).then {

                userCommunicationRepository.update(userOptin).then { UserCommunication newUserOptin ->
                    newUserOptin = userOptinFilter.filterForGet(newUserOptin, null)
                    return Promise.pure(newUserOptin)
                }
            }
        }
    }

    @Override
    Promise<UserCommunication> put(UserCommunicationId userOptinId, UserCommunication userOptin) {
        if (userOptinId == null) {
            throw new IllegalArgumentException('userOptinId is null')
        }

        if (userOptin == null) {
            throw new IllegalArgumentException('userOptin is null')
        }

        return userCommunicationRepository.get(userOptinId).then { UserCommunication oldUserOptin ->
            if (oldUserOptin == null) {
                throw AppErrors.INSTANCE.userOptinNotFound(userOptinId).exception()
            }

            userOptin = userOptinFilter.filterForPut(userOptin, oldUserOptin)

            return userOptinValidator.validateForUpdate(userOptinId, userOptin, oldUserOptin).then {
                userCommunicationRepository.update(userOptin).then { UserCommunication newUserOptin ->
                    newUserOptin = userOptinFilter.filterForGet(newUserOptin, null)
                    return Promise.pure(newUserOptin)
                }
            }
        }
    }

    @Override
    Promise<Void> delete(UserCommunicationId userOptinId) {
        return userOptinValidator.validateForGet(userOptinId).then {
            userCommunicationRepository.delete(userOptinId)

            return Promise.pure(null)
        }
    }

    @Override
    Promise<Results<UserCommunication>> list(UserOptinListOptions listOptions) {
        return userOptinValidator.validateForSearch(listOptions).then {
            userCommunicationRepository.search(listOptions).then { List<UserCommunication> userOptinList ->
                def result = new Results<UserCommunication>(items: [])

                userOptinList.each { UserCommunication newUserOptin ->
                    if (newUserOptin != null) {
                        newUserOptin = userOptinFilter.filterForGet(newUserOptin,
                                listOptions.properties?.split(',') as List<String>)
                    }

                    if (newUserOptin != null) {
                        result.items.add(newUserOptin)
                    }
                }

                return Promise.pure(result)
            }
        }
    }
}
