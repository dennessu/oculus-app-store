package com.junbo.identity.rest.resource.v1

import com.junbo.authorization.AuthorizeContext
import com.junbo.authorization.AuthorizeService
import com.junbo.authorization.RightsScope
import com.junbo.common.id.Id
import com.junbo.common.id.UserId
import com.junbo.common.id.UserTeleId
import com.junbo.common.model.Results
import com.junbo.common.rs.Created201Marker
import com.junbo.identity.auth.UserPropertyAuthorizeCallbackFactory
import com.junbo.identity.clientproxy.TeleSign
import com.junbo.identity.core.service.filter.UserTeleFilter
import com.junbo.identity.core.service.validator.UserTeleValidator
import com.junbo.identity.data.repository.UserTeleRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.UserTeleCode
import com.junbo.identity.spec.v1.option.list.UserTeleListOptions
import com.junbo.identity.spec.v1.option.model.UserTeleGetOptions
import com.junbo.identity.spec.v1.resource.UserTeleResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Created by liangfu on 4/24/14.
 */
@CompileStatic
@Transactional
class UserTeleResourceImpl implements UserTeleResource {

    @Autowired
    private UserTeleRepository userTeleRepository

    @Autowired
    private UserTeleFilter userTeleFilter

    @Autowired
    private UserTeleValidator userTeleValidator

    @Autowired
    private TeleSign teleSign

    @Autowired
    private AuthorizeService authorizeService

    @Autowired
    private UserPropertyAuthorizeCallbackFactory authorizeCallbackFactory

    @Override
    Promise<UserTeleCode> create(UserId userId, UserTeleCode userTeleCode) {
        if (userTeleCode == null) {
            throw new IllegalArgumentException('userTeleCode is null')
        }

        if (userTeleCode.userId != null && userTeleCode.userId != userId) {
            throw AppErrors.INSTANCE.fieldInvalid('userId', userId.toString()).exception()
        }

        def callback = authorizeCallbackFactory.create(userId)
        return RightsScope.with(authorizeService.authorize(callback)) {
            if (!AuthorizeContext.hasRights('create')) {
                throw AppErrors.INSTANCE.invalidAccess().exception()
            }

            userTeleCode = userTeleFilter.filterForCreate(userTeleCode)

            return userTeleValidator.validateForCreate(userId, userTeleCode).then {
                return teleSign.verifyCode(userTeleCode).then {
                    return userTeleRepository.create(userTeleCode).then { UserTeleCode newUserTeleCode ->
                        Created201Marker.mark((Id) newUserTeleCode.id)

                        newUserTeleCode = userTeleFilter.filterForGet(newUserTeleCode, null)
                        return Promise.pure(newUserTeleCode)
                    }
                }
            }
        }
    }

    @Override
    Promise<UserTeleCode> get(UserId userId, UserTeleId userTeleId, UserTeleGetOptions getOptions) {
        if (getOptions == null) {
            throw new IllegalArgumentException('getOptions is null')
        }

        if (userId == null) {
            throw AppErrors.INSTANCE.fieldRequired('userId').exception()
        }

        def callback = authorizeCallbackFactory.create(userId)
        return RightsScope.with(authorizeService.authorize(callback)) {
            if (!AuthorizeContext.hasRights('read')) {
                throw AppErrors.INSTANCE.invalidAccess().exception()
            }

            return userTeleValidator.validateForGet(userId, userTeleId).then { UserTeleCode newUserTeleCode ->
                newUserTeleCode = userTeleFilter.filterForGet(newUserTeleCode,
                        getOptions.properties?.split(',') as List<String>)

                return Promise.pure(newUserTeleCode)
            }
        }
    }

    @Override
    Promise<UserTeleCode> patch(UserId userId, UserTeleId userTeleId, UserTeleCode userTeleCode) {
        if (userId == null) {
            throw new IllegalArgumentException('userId is null')
        }

        if (userTeleId == null) {
            throw new IllegalArgumentException('userTeleId is null')
        }

        if (userTeleCode == null) {
            throw new IllegalArgumentException('userTeleCode is null')
        }

        def callback = authorizeCallbackFactory.create(userId)
        return RightsScope.with(authorizeService.authorize(callback)) {
            if (!AuthorizeContext.hasRights('update')) {
                throw AppErrors.INSTANCE.invalidAccess().exception()
            }

            return userTeleRepository.get(userTeleId).then { UserTeleCode oldUserTeleCode ->
                if (oldUserTeleCode == null) {
                    throw AppErrors.INSTANCE.userTeleCodeNotFound(userTeleId).exception()
                }

                userTeleCode = userTeleFilter.filterForPatch(userTeleCode, oldUserTeleCode)

                return userTeleValidator.validateForUpdate(userId, userTeleId, userTeleCode, oldUserTeleCode).then {

                    return userTeleRepository.update(userTeleCode).then { UserTeleCode newUserTele ->
                        newUserTele = userTeleFilter.filterForGet(newUserTele, null)
                        return Promise.pure(newUserTele)
                    }
                }
            }
        }
    }

    @Override
    Promise<UserTeleCode> put(UserId userId, UserTeleId userTeleId, UserTeleCode userTeleCode) {
        if (userId == null) {
            throw new IllegalArgumentException('userId is null')
        }

        if (userTeleId == null) {
            throw new IllegalArgumentException('userTeleId is null')
        }

        if (userTeleCode == null) {
            throw new IllegalArgumentException('userTeleCode is null')
        }

        def callback = authorizeCallbackFactory.create(userId)
        return RightsScope.with(authorizeService.authorize(callback)) {
            if (!AuthorizeContext.hasRights('update')) {
                throw AppErrors.INSTANCE.invalidAccess().exception()
            }

            return userTeleRepository.get(userTeleId).then { UserTeleCode oldUserTeleCode ->
                if (oldUserTeleCode == null) {
                    throw AppErrors.INSTANCE.userTeleCodeNotFound(userTeleId).exception()
                }

                userTeleCode = userTeleFilter.filterForPut(userTeleCode, oldUserTeleCode)

                return userTeleValidator.validateForUpdate(userId, userTeleId, userTeleCode, oldUserTeleCode).then {
                    return userTeleRepository.update(userTeleCode).then { UserTeleCode newUserTeleCode ->
                        newUserTeleCode = userTeleFilter.filterForGet(newUserTeleCode, null)
                        return Promise.pure(newUserTeleCode)
                    }
                }
            }
        }
    }

    @Override
    Promise<Void> delete(UserId userId, UserTeleId userTeleId) {
        if (userId == null) {
            throw AppErrors.INSTANCE.fieldRequired('userId').exception()
        }

        def callback = authorizeCallbackFactory.create(userId)
        return RightsScope.with(authorizeService.authorize(callback)) {
            if (!AuthorizeContext.hasRights('delete')) {
                throw AppErrors.INSTANCE.invalidAccess().exception()
            }

            return userTeleValidator.validateForGet(userId, userTeleId).then {
                return userTeleRepository.delete(userTeleId)
            }
        }
    }

    @Override
    Promise<Results<UserTeleCode>> list(UserId userId, UserTeleListOptions listOptions) {
        if (listOptions == null) {
            throw new IllegalArgumentException('listOptions is null')
        }

        if (userId == null) {
            throw AppErrors.INSTANCE.fieldRequired('userId').exception()
        }

        def callback = authorizeCallbackFactory.create(userId)
        return RightsScope.with(authorizeService.authorize(callback)) {
            def result = new Results<UserTeleCode>(items: [])
            if (!AuthorizeContext.hasRights('read')) {
                return Promise.pure(result)
            }

            listOptions.setUserId(userId)

            return userTeleValidator.validateForSearch(listOptions).then {
                return search(listOptions).then { List<UserTeleCode> userTeleCodeList ->
                    userTeleCodeList.each { UserTeleCode newUserTeleCode ->
                        if (newUserTeleCode != null) {
                            newUserTeleCode = userTeleFilter.filterForGet(newUserTeleCode,
                                    listOptions.properties?.split(',') as List<String>)

                            result.items.add(newUserTeleCode)
                        }
                    }

                    return Promise.pure(result)
                }
            }
        }
    }

    private Promise<List<UserTeleCode>> search(UserTeleListOptions listOptions) {
        if (listOptions.userId != null && listOptions.phoneNumber != null) {
            return userTeleRepository.searchTeleCodeByUserIdAndPhone(listOptions.userId, listOptions.phoneNumber,
                    listOptions.limit, listOptions.offset)
        } else {
            throw new IllegalArgumentException('Unsupported search operation.')
        }
    }
}
