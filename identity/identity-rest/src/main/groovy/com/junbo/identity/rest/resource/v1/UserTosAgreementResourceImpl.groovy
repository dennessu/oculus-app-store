package com.junbo.identity.rest.resource.v1

import com.junbo.common.id.Id
import com.junbo.common.id.UserId
import com.junbo.common.id.UserTosAgreementId
import com.junbo.common.model.Results
import com.junbo.common.rs.Created201Marker
import com.junbo.identity.core.service.filter.UserTosFilter
import com.junbo.identity.core.service.validator.UserTosValidator
import com.junbo.identity.data.repository.UserTosRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.UserTosAgreement
import com.junbo.identity.spec.v1.option.list.UserTosAgreementListOptions
import com.junbo.identity.spec.v1.option.model.UserTosAgreementGetOptions
import com.junbo.identity.spec.v1.resource.UserTosAgreementResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Created by liangfu on 4/11/14.
 */
@Transactional
@CompileStatic
class UserTosAgreementResourceImpl implements UserTosAgreementResource {

    @Autowired
    private UserTosRepository userTosRepository

    @Autowired
    private UserTosFilter userTosFilter

    @Autowired
    private UserTosValidator userTosValidator

    @Override
    Promise<UserTosAgreement> create(UserId userId, UserTosAgreement userTos) {
        if (userTos == null) {
            throw new IllegalArgumentException('userTos is null')
        }

        userTos = userTosFilter.filterForCreate(userTos)

        return userTosValidator.validateForCreate(userId, userTos).then {
            return userTosRepository.create(userTos).then { UserTosAgreement newUserTos ->
                Created201Marker.mark((Id) newUserTos.id)

                newUserTos = userTosFilter.filterForGet(newUserTos, null)
                return Promise.pure(newUserTos)
            }
        }
    }

    @Override
    Promise<UserTosAgreement> get(UserId userId,
                                  UserTosAgreementId userTosAgreementId, UserTosAgreementGetOptions getOptions) {
        if (getOptions == null) {
            throw new IllegalArgumentException('getOptions is null')
        }

        return userTosValidator.validateForGet(userId, userTosAgreementId).then { UserTosAgreement newUserTos ->
            newUserTos = userTosFilter.filterForGet(newUserTos,
                    getOptions.properties?.split(',') as List<String>)

            return Promise.pure(newUserTos)
        }
    }

    @Override
    Promise<UserTosAgreement> patch(UserId userId, UserTosAgreementId userTosAgreementId,
                                    UserTosAgreement userTos) {
        if (userId == null) {
            throw new IllegalArgumentException('userId is null')
        }

        if (userTosAgreementId == null) {
            throw new IllegalArgumentException('userTosId is null')
        }

        if (userTos == null) {
            throw new IllegalArgumentException('userTos is null')
        }

        return userTosRepository.get(userTosAgreementId).then { UserTosAgreement oldUserTos ->
            if (oldUserTos == null) {
                throw AppErrors.INSTANCE.userTosAgreementNotFound(userTosAgreementId).exception()
            }

            userTos = userTosFilter.filterForPatch(userTos, oldUserTos)

            return userTosValidator.validateForUpdate(userId, userTosAgreementId, userTos, oldUserTos).then {

                return userTosRepository.update(userTos).then { UserTosAgreement newUserTos ->
                    newUserTos = userTosFilter.filterForGet(newUserTos, null)
                    return Promise.pure(newUserTos)
                }
            }
        }
    }

    @Override
    Promise<UserTosAgreement> put(UserId userId, UserTosAgreementId userTosAgreementId,
                                  UserTosAgreement userTos) {
        if (userId == null) {
            throw new IllegalArgumentException('userId is null')
        }

        if (userTosAgreementId == null) {
            throw new IllegalArgumentException('userTosId is null')
        }

        if (userTos == null) {
            throw new IllegalArgumentException('userTos is null')
        }

        return userTosRepository.get(userTosAgreementId).then { UserTosAgreement oldUserTos ->
            if (oldUserTos == null) {
                throw AppErrors.INSTANCE.userTosAgreementNotFound(userTosAgreementId).exception()
            }

            userTos = userTosFilter.filterForPut(userTos, oldUserTos)

            return userTosValidator.validateForUpdate(userId, userTosAgreementId, userTos, oldUserTos).then {
                return userTosRepository.update(userTos).then { UserTosAgreement newUserTos ->
                    newUserTos = userTosFilter.filterForGet(newUserTos, null)
                    return Promise.pure(newUserTos)
                }
            }
        }
    }

    @Override
    Promise<Void> delete(UserId userId, UserTosAgreementId userTosAgreementId) {
        return userTosValidator.validateForGet(userId, userTosAgreementId).then {
            return userTosRepository.delete(userTosAgreementId)
        }
    }

    @Override
    Promise<Results<UserTosAgreement>> list(UserId userId, UserTosAgreementListOptions listOptions) {
        if (listOptions == null) {
            throw new IllegalArgumentException('listOptions is null')
        }
        listOptions.setUserId(userId)

        return userTosValidator.validateForSearch(listOptions).then {
            return userTosRepository.search(listOptions).then { List<UserTosAgreement> userTosList ->
                def result = new Results<UserTosAgreement>(items: [])

                userTosList.each { UserTosAgreement newUserTos ->
                    if (newUserTos != null) {
                        newUserTos = userTosFilter.filterForGet(newUserTos,
                                listOptions.properties?.split(',') as List<String>)
                    }

                    if (newUserTos != null) {
                        result.items.add(newUserTos)
                    }
                }

                return Promise.pure(result)
            }
        }
    }
}
