package com.junbo.identity.rest.resource.v1

import com.junbo.common.id.Id
import com.junbo.common.id.UserPiiId
import com.junbo.common.model.Results
import com.junbo.identity.core.service.Created201Marker
import com.junbo.identity.core.service.filter.UserPiiFilter
import com.junbo.identity.core.service.validator.UserPiiValidator
import com.junbo.identity.data.repository.UserPiiRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.UserPii
import com.junbo.identity.spec.v1.option.list.UserPiiListOptions
import com.junbo.identity.spec.v1.option.model.UserPiiGetOptions
import com.junbo.identity.spec.v1.resource.UserPiiResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Created by liangfu on 4/10/14.
 */
@Transactional
@CompileStatic
class UserPiiResourceImpl implements UserPiiResource {

    @Autowired
    private Created201Marker created201Marker

    @Autowired
    private UserPiiRepository userPiiRepository

    @Autowired
    private UserPiiValidator userPiiValidator

    @Autowired
    private UserPiiFilter userPiiFilter

    @Override
    Promise<UserPii> create(UserPii userPii) {
        if (userPii == null) {
            throw new IllegalArgumentException('userPii is null')
        }

        userPii = userPiiFilter.filterForCreate(userPii)

        userPiiValidator.validateForCreate(userPii).then {
            userPiiRepository.create(userPii).then { UserPii newUserPii ->
                return userPiiValidator.validateForGet((UserPiiId)newUserPii.id).then { UserPii newCreated ->
                    created201Marker.mark((Id)newCreated.id)

                    newUserPii = userPiiFilter.filterForGet(newCreated, null)
                    return Promise.pure(newCreated)
                }
            }
        }
    }

    @Override
    Promise<UserPii> get(UserPiiId userPiiId, UserPiiGetOptions getOptions) {
        if (getOptions == null) {
            throw new IllegalArgumentException('getOptions is null')
        }

        userPiiValidator.validateForGet(userPiiId).then { UserPii newUserPii ->
            newUserPii = userPiiFilter.filterForGet(newUserPii,
                getOptions.properties?.split(',') as List<String>)

            return Promise.pure(newUserPii)
        }
    }

    @Override
    Promise<UserPii> patch(UserPiiId userPiiId, UserPii userPii) {
        if (userPiiId == null) {
            throw new IllegalArgumentException('userPiiId is null')
        }

        if (userPii == null) {
            throw new IllegalArgumentException('userPii is null')
        }

        return userPiiValidator.validateForGet(userPiiId).then { UserPii oldUserPii ->
            if (oldUserPii == null) {
                throw AppErrors.INSTANCE.userPiiNotFound(userPiiId).exception()
            }

            userPii = userPiiFilter.filterForPatch(userPii, oldUserPii)

            userPiiValidator.validateForUpdate(userPiiId, userPii, oldUserPii).then {

                userPiiRepository.update(userPii).then { UserPii newUserPii ->
                    return userPiiValidator.validateForGet((UserPiiId)newUserPii.id).then { UserPii newCreated ->

                        newCreated = userPiiFilter.filterForGet(newCreated, null)
                        return Promise.pure(newCreated)
                    }
                }
            }
        }
    }

    @Override
    Promise<UserPii> put(UserPiiId userPiiId, UserPii userPii) {
        if (userPiiId == null) {
            throw new IllegalArgumentException('userPiiId is null')
        }

        if (userPii == null) {
            throw new IllegalArgumentException('userPii is null')
        }

        return userPiiValidator.validateForGet(userPiiId).then { UserPii oldUserPii ->
            if (oldUserPii == null) {
                throw AppErrors.INSTANCE.userPiiNotFound(userPiiId).exception()
            }

            userPii = userPiiFilter.filterForPut(userPii, oldUserPii)

            return userPiiValidator.validateForUpdate(userPiiId, userPii, oldUserPii)
        }.then {
            return userPiiRepository.update(userPii)
        }.then { UserPii newUserPii ->
            return userPiiValidator.validateForGet((UserPiiId)newUserPii.id).then { UserPii newCreated ->
                newCreated = userPiiFilter.filterForGet(newCreated, null)
                return Promise.pure(newCreated)
            }
        }
    }

    @Override
    Promise<Void> delete(UserPiiId userPiiId) {
        return userPiiValidator.validateForGet(userPiiId).then {
            userPiiRepository.delete(userPiiId)

            return Promise.pure(null)
        }
    }

    @Override
    Promise<Results<UserPii>> list(UserPiiListOptions listOptions) {
        if (listOptions == null) {
            throw new IllegalArgumentException('listOptions is null')
        }

        return userPiiValidator.validateForSearch(listOptions).then {
            userPiiRepository.search(listOptions).then { List<UserPii> userPiiList ->
                def result = new Results<UserPii>(items: [])

                userPiiList.each { UserPii newUserPii ->
                    if (newUserPii != null) {
                        newUserPii = userPiiFilter.filterForGet(newUserPii,
                                listOptions.properties?.split(',') as List<String>)
                    }

                    if (newUserPii != null) {
                        result.items.add(newUserPii)
                    }
                }

                return Promise.pure(result)
            }
        }
    }
}
