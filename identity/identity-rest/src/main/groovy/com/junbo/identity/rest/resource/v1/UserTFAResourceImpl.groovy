package com.junbo.identity.rest.resource.v1

import com.junbo.authorization.AuthorizeContext
import com.junbo.authorization.AuthorizeService
import com.junbo.authorization.RightsScope
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.UserId
import com.junbo.common.id.UserTFAId
import com.junbo.common.model.Results
import com.junbo.common.rs.Created201Marker
import com.junbo.identity.auth.UserPropertyAuthorizeCallbackFactory
import com.junbo.identity.clientproxy.TeleSign
import com.junbo.identity.core.service.filter.UserTFAFilter
import com.junbo.identity.core.service.validator.UserTFAValidator
import com.junbo.identity.data.repository.UserTFARepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.UserTFA
import com.junbo.identity.spec.v1.option.list.UserTFAListOptions
import com.junbo.identity.spec.v1.option.model.UserTFAGetOptions
import com.junbo.identity.spec.v1.resource.UserTFAResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Created by liangfu on 4/24/14.
 */
@CompileStatic
@Transactional
class UserTFAResourceImpl implements UserTFAResource {

    @Autowired
    private UserTFARepository userTFARepository

    @Autowired
    private UserTFAFilter userTFAFilter

    @Autowired
    private UserTFAValidator userTFAValidator

    @Autowired
    private TeleSign teleSign

    @Autowired
    private AuthorizeService authorizeService

    @Autowired
    private UserPropertyAuthorizeCallbackFactory authorizeCallbackFactory

    @Override
    Promise<UserTFA> create(UserId userId, UserTFA userTeleCode) {
        if (userTeleCode == null) {
            throw new IllegalArgumentException('userTFA is null')
        }

        if (userTeleCode.userId != null && userTeleCode.userId != userId) {
            throw AppCommonErrors.INSTANCE.fieldNotWritable('userId', userTeleCode.userId, userId).exception()
        }

        def callback = authorizeCallbackFactory.create(userId)
        return RightsScope.with(authorizeService.authorize(callback)) {
            if (!AuthorizeContext.hasRights('create')) {
                throw AppCommonErrors.INSTANCE.forbidden().exception()
            }

            userTeleCode = userTFAFilter.filterForCreate(userTeleCode)

            return userTFAValidator.validateForCreate(userId, userTeleCode).then {
                return teleSign.verifyCode(userTeleCode).then {
                    return userTFARepository.create(userTeleCode).then { UserTFA newUserTeleCode ->
                        Created201Marker.mark(newUserTeleCode.getId())

                        newUserTeleCode = userTFAFilter.filterForGet(newUserTeleCode, null)
                        return Promise.pure(newUserTeleCode)
                    }
                }
            }
        }
    }

    @Override
    Promise<UserTFA> get(UserId userId, UserTFAId userTFAId, UserTFAGetOptions getOptions) {
        if (getOptions == null) {
            throw new IllegalArgumentException('getOptions is null')
        }

        if (userId == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('userId').exception()
        }

        def callback = authorizeCallbackFactory.create(userId)
        return RightsScope.with(authorizeService.authorize(callback)) {
            if (!AuthorizeContext.hasRights('read')) {
                throw AppCommonErrors.INSTANCE.forbidden().exception()
            }

            return userTFAValidator.validateForGet(userId, userTFAId).then { UserTFA newUserTeleCode ->
                newUserTeleCode = userTFAFilter.filterForGet(newUserTeleCode,
                        getOptions.properties?.split(',') as List<String>)

                return Promise.pure(newUserTeleCode)
            }
        }
    }

    @Override
    Promise<UserTFA> patch(UserId userId, UserTFAId userTFAId, UserTFA userTFA) {
        if (userId == null) {
            throw new IllegalArgumentException('userId is null')
        }

        if (userTFAId == null) {
            throw new IllegalArgumentException('userTFAId is null')
        }

        if (userTFA == null) {
            throw new IllegalArgumentException('userTFA is null')
        }

        def callback = authorizeCallbackFactory.create(userId)
        return RightsScope.with(authorizeService.authorize(callback)) {
            if (!AuthorizeContext.hasRights('update')) {
                throw AppCommonErrors.INSTANCE.forbidden().exception()
            }

            return userTFARepository.get(userTFAId).then { UserTFA oldUserTeleCode ->
                if (oldUserTeleCode == null) {
                    throw AppErrors.INSTANCE.userTFANotFound(userTFAId).exception()
                }

                userTFA = userTFAFilter.filterForPatch(userTFA, oldUserTeleCode)

                return userTFAValidator.validateForUpdate(userId, userTFAId, userTFA, oldUserTeleCode).then {

                    return userTFARepository.update(userTFA, oldUserTeleCode).then { UserTFA newUserTele ->
                        newUserTele = userTFAFilter.filterForGet(newUserTele, null)
                        return Promise.pure(newUserTele)
                    }
                }
            }
        }
    }

    @Override
    Promise<UserTFA> put(UserId userId, UserTFAId userTFAId, UserTFA userTFA) {
        if (userId == null) {
            throw new IllegalArgumentException('userId is null')
        }

        if (userTFAId == null) {
            throw new IllegalArgumentException('userTFAId is null')
        }

        if (userTFA == null) {
            throw new IllegalArgumentException('userTFA is null')
        }

        def callback = authorizeCallbackFactory.create(userId)
        return RightsScope.with(authorizeService.authorize(callback)) {
            if (!AuthorizeContext.hasRights('update')) {
                throw AppCommonErrors.INSTANCE.forbidden().exception()
            }

            return userTFARepository.get(userTFAId).then { UserTFA oldUserTeleCode ->
                if (oldUserTeleCode == null) {
                    throw AppErrors.INSTANCE.userTFANotFound(userTFAId).exception()
                }

                userTFA = userTFAFilter.filterForPut(userTFA, oldUserTeleCode)

                return userTFAValidator.validateForUpdate(userId, userTFAId, userTFA, oldUserTeleCode).then {
                    return userTFARepository.update(userTFA, oldUserTeleCode).then { UserTFA newUserTeleCode ->
                        newUserTeleCode = userTFAFilter.filterForGet(newUserTeleCode, null)
                        return Promise.pure(newUserTeleCode)
                    }
                }
            }
        }
    }

    @Override
    Promise<Void> delete(UserId userId, UserTFAId userTFAId) {
        if (userId == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('userId').exception()
        }

        def callback = authorizeCallbackFactory.create(userId)
        return RightsScope.with(authorizeService.authorize(callback)) {
            if (!AuthorizeContext.hasRights('delete')) {
                throw AppCommonErrors.INSTANCE.forbidden().exception()
            }

            return userTFAValidator.validateForGet(userId, userTFAId).then {
                return userTFARepository.delete(userTFAId)
            }
        }
    }

    @Override
    Promise<Results<UserTFA>> list(UserId userId, UserTFAListOptions listOptions) {
        if (listOptions == null) {
            throw new IllegalArgumentException('listOptions is null')
        }

        if (userId == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('userId').exception()
        }

        def callback = authorizeCallbackFactory.create(userId)
        return RightsScope.with(authorizeService.authorize(callback)) {
            def result = new Results<UserTFA>(items: [])
            if (!AuthorizeContext.hasRights('read')) {
                return Promise.pure(result)
            }

            listOptions.setUserId(userId)

            return userTFAValidator.validateForSearch(listOptions).then {
                return search(listOptions).then { List<UserTFA> userTeleCodeList ->
                    userTeleCodeList.each { UserTFA newUserTeleCode ->
                        if (newUserTeleCode != null) {
                            newUserTeleCode = userTFAFilter.filterForGet(newUserTeleCode,
                                    listOptions.properties?.split(',') as List<String>)

                            result.items.add(newUserTeleCode)
                        }
                    }

                    return Promise.pure(result)
                }
            }
        }
    }

    private Promise<List<UserTFA>> search(UserTFAListOptions listOptions) {
        if (listOptions.userId != null && listOptions.personalInfo != null) {
            return userTFARepository.searchTFACodeByUserIdAndPersonalInfoId(listOptions.userId, listOptions.personalInfo,
                    listOptions.limit, listOptions.offset)
        } else {
            throw new IllegalArgumentException('Unsupported search operation.')
        }
    }
}
