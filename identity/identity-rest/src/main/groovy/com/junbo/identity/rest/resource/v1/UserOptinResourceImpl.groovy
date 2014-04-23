package com.junbo.identity.rest.resource.v1

import com.junbo.common.id.Id
import com.junbo.common.id.UserOptinId
import com.junbo.common.model.Results
import com.junbo.identity.core.service.Created201Marker
import com.junbo.identity.core.service.filter.UserOptinFilter
import com.junbo.identity.core.service.validator.UserOptinValidator
import com.junbo.identity.data.repository.UserOptinRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.UserOptin
import com.junbo.identity.spec.v1.option.list.UserOptinListOptions
import com.junbo.identity.spec.v1.option.model.UserOptinGetOptions
import com.junbo.identity.spec.v1.resource.UserOptinResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Created by liangfu on 4/11/14.
 */
@Transactional
@CompileStatic
class UserOptinResourceImpl implements UserOptinResource {

    @Autowired
    private UserOptinRepository userOptinRepository

    @Autowired
    private Created201Marker created201Marker

    @Autowired
    private UserOptinFilter userOptinFilter

    @Autowired
    private UserOptinValidator userOptinValidator

    @Override
    Promise<UserOptin> create(UserOptin userOptin) {
        if (userOptin == null) {
            throw new IllegalArgumentException('userOptin is null')
        }

        userOptin = userOptinFilter.filterForCreate(userOptin)

        userOptinValidator.validateForCreate(userOptin).then {
            userOptinRepository.create(userOptin).then { UserOptin newUserOptin ->
                created201Marker.mark((Id)newUserOptin.id)

                newUserOptin = userOptinFilter.filterForGet(newUserOptin, null)
                return Promise.pure(newUserOptin)
            }
        }
    }

    @Override
    Promise<UserOptin> get(UserOptinId userOptinId, UserOptinGetOptions getOptions) {
        if (getOptions == null) {
            throw new IllegalArgumentException('getOptions is null')
        }

        userOptinValidator.validateForGet(userOptinId).then { UserOptin newUserOptin ->
            newUserOptin = userOptinFilter.filterForGet(newUserOptin,
                    getOptions.properties?.split(',') as List<String>)

            return Promise.pure(newUserOptin)
        }
    }

    @Override
    Promise<UserOptin> patch(UserOptinId userOptinId, UserOptin userOptin) {
        if (userOptinId == null) {
            throw new IllegalArgumentException('userOptinId is null')
        }

        if (userOptin == null) {
            throw new IllegalArgumentException('userOptin is null')
        }

        return userOptinRepository.get(userOptinId).then { UserOptin oldUserOptin ->
            if (oldUserOptin == null) {
                throw AppErrors.INSTANCE.userOptinNotFound(userOptinId).exception()
            }

            userOptin = userOptinFilter.filterForPatch(userOptin, oldUserOptin)

            userOptinValidator.validateForUpdate(userOptinId, userOptin, oldUserOptin).then {

                userOptinRepository.update(userOptin).then { UserOptin newUserOptin ->
                    newUserOptin = userOptinFilter.filterForGet(newUserOptin, null)
                    return Promise.pure(newUserOptin)
                }
            }
        }
    }

    @Override
    Promise<UserOptin> put(UserOptinId userOptinId, UserOptin userOptin) {
        if (userOptinId == null) {
            throw new IllegalArgumentException('userOptinId is null')
        }

        if (userOptin == null) {
            throw new IllegalArgumentException('userOptin is null')
        }

        return userOptinRepository.get(userOptinId).then { UserOptin oldUserOptin ->
            if (oldUserOptin == null) {
                throw AppErrors.INSTANCE.userOptinNotFound(userOptinId).exception()
            }

            userOptin = userOptinFilter.filterForPut(userOptin, oldUserOptin)

            return userOptinValidator.validateForUpdate(userOptinId, userOptin, oldUserOptin).then {
                userOptinRepository.update(userOptin).then { UserOptin newUserOptin ->
                    newUserOptin = userOptinFilter.filterForGet(newUserOptin, null)
                    return Promise.pure(newUserOptin)
                }
            }
        }
    }

    @Override
    Promise<Void> delete(UserOptinId userOptinId) {
        return userOptinValidator.validateForGet(userOptinId).then {
            userOptinRepository.delete(userOptinId)

            return Promise.pure(null)
        }
    }

    @Override
    Promise<Results<UserOptin>> list(UserOptinListOptions listOptions) {
        return userOptinValidator.validateForSearch(listOptions).then {
            userOptinRepository.search(listOptions).then { List<UserOptin> userOptinList ->
                def result = new Results<UserOptin>(items: [])

                userOptinList.each { UserOptin newUserOptin ->
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
