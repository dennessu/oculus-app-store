package com.junbo.identity.rest.resource.v1

import com.junbo.authorization.AuthorizeContext
import com.junbo.authorization.AuthorizeService
import com.junbo.authorization.RightsScope
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.UserTosAgreementId
import com.junbo.common.model.Results
import com.junbo.common.rs.Created201Marker
import com.junbo.identity.auth.UserPropertyAuthorizeCallbackFactory
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

    @Autowired
    private AuthorizeService authorizeService

    @Autowired
    private UserPropertyAuthorizeCallbackFactory authorizeCallbackFactory

    @Override
    Promise<UserTosAgreement> create(UserTosAgreement userTos) {
        if (userTos == null) {
            throw new IllegalArgumentException('userTos is null')
        }

        userTos = userTosFilter.filterForCreate(userTos)

        return userTosValidator.validateForCreate(userTos).then {
            def callback = authorizeCallbackFactory.create(userTos.userId)
            return RightsScope.with(authorizeService.authorize(callback)) {
                if (!AuthorizeContext.hasRights('create')) {
                    throw AppCommonErrors.INSTANCE.forbidden().exception()
                }

                return userTosRepository.create(userTos).then { UserTosAgreement newUserTos ->
                    Created201Marker.mark(newUserTos.getId())

                    newUserTos = userTosFilter.filterForGet(newUserTos, null)
                    return Promise.pure(newUserTos)
                }
            }
        }
    }

    @Override
    Promise<UserTosAgreement> get(UserTosAgreementId userTosAgreementId, UserTosAgreementGetOptions getOptions) {
        if (getOptions == null) {
            throw new IllegalArgumentException('getOptions is null')
        }

        if (userTosAgreementId == null) {
            throw new IllegalArgumentException('userTosAgreementId is null')
        }

        return userTosValidator.validateForGet(userTosAgreementId).then { UserTosAgreement newUserTos ->
            def callback = authorizeCallbackFactory.create(newUserTos.userId)
            return RightsScope.with(authorizeService.authorize(callback)) {
                if (!AuthorizeContext.hasRights('read')) {
                    throw AppCommonErrors.INSTANCE.forbidden().exception()
                }

                newUserTos = userTosFilter.filterForGet(newUserTos,
                        getOptions.properties?.split(',') as List<String>)

                return Promise.pure(newUserTos)
            }
        }
    }

    Promise<UserTosAgreement> patch(UserTosAgreementId userTosAgreementId, UserTosAgreement userTos) {
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

            def callback = authorizeCallbackFactory.create(oldUserTos.userId)
            return RightsScope.with(authorizeService.authorize(callback)) {
                if (!AuthorizeContext.hasRights('update')) {
                    throw AppCommonErrors.INSTANCE.forbidden().exception()
                }

                userTos = userTosFilter.filterForPatch(userTos, oldUserTos)

                return userTosValidator.validateForUpdate(userTosAgreementId, userTos, oldUserTos).then {

                    return userTosRepository.update(userTos, oldUserTos).then { UserTosAgreement newUserTos ->
                        newUserTos = userTosFilter.filterForGet(newUserTos, null)
                        return Promise.pure(newUserTos)
                    }
                }
            }
        }
    }

    Promise<UserTosAgreement> put(UserTosAgreementId userTosAgreementId, UserTosAgreement userTos) {
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

            def callback = authorizeCallbackFactory.create(oldUserTos.userId)
            return RightsScope.with(authorizeService.authorize(callback)) {
                if (!AuthorizeContext.hasRights('update')) {
                    throw AppCommonErrors.INSTANCE.forbidden().exception()
                }

                userTos = userTosFilter.filterForPut(userTos, oldUserTos)

                return userTosValidator.validateForUpdate(userTosAgreementId, userTos, oldUserTos).then {
                    return userTosRepository.update(userTos, oldUserTos).then { UserTosAgreement newUserTos ->
                        newUserTos = userTosFilter.filterForGet(newUserTos, null)
                        return Promise.pure(newUserTos)
                    }
                }
            }
        }
    }

    Promise<Void> delete(UserTosAgreementId userTosAgreementId) {
        return userTosValidator.validateForGet(userTosAgreementId).then { UserTosAgreement userTos ->
            def callback = authorizeCallbackFactory.create(userTos.userId)
            return RightsScope.with(authorizeService.authorize(callback)) {
                if (!AuthorizeContext.hasRights('delete')) {
                    throw AppCommonErrors.INSTANCE.forbidden().exception()
                }

                return userTosRepository.delete(userTosAgreementId)
            }
        }
    }

    @Override
    Promise<Results<UserTosAgreement>> list(UserTosAgreementListOptions listOptions) {
        if (listOptions == null) {
            throw new IllegalArgumentException('listOptions is null')
        }

        return userTosValidator.validateForSearch(listOptions).then {
            return search(listOptions).then { List<UserTosAgreement> userTosList ->
                def result = new Results<UserTosAgreement>(items: [])

                return Promise.each(userTosList) { UserTosAgreement newUserTos ->
                    def callback = authorizeCallbackFactory.create(newUserTos.userId)
                    return RightsScope.with(authorizeService.authorize(callback)) {
                        if (newUserTos != null) {
                            newUserTos = userTosFilter.filterForGet(newUserTos,
                                    listOptions.properties?.split(',') as List<String>)
                        }

                        if (newUserTos != null && AuthorizeContext.hasRights('read')) {
                            result.items.add(newUserTos)
                            return Promise.pure(newUserTos)
                        } else {
                            return Promise.pure(null)
                        }
                    }
                }.then {
                    return Promise.pure(result)
                }
            }
        }
    }

    Promise<List<UserTosAgreement>> search(UserTosAgreementListOptions listOptions) {
        if (listOptions.userId != null && listOptions.tosId != null) {
            return userTosRepository.searchByUserIdAndTosId(listOptions.userId, listOptions.tosId, listOptions.limit, listOptions.offset)
        } else if (listOptions.userId != null) {
            return userTosRepository.searchByUserId(listOptions.userId, listOptions.limit, listOptions.offset)
        } else if (listOptions.tosId != null) {
            return userTosRepository.searchByTosId(listOptions.tosId, listOptions.limit, listOptions.offset)
        } else {
            throw AppCommonErrors.INSTANCE.invalidOperation('unsupported search operation').exception()
        }
    }
}
