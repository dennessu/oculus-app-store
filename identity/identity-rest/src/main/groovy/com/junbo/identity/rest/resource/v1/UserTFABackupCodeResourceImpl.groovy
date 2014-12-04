package com.junbo.identity.rest.resource.v1

import com.junbo.authorization.AuthorizeContext
import com.junbo.authorization.AuthorizeService
import com.junbo.authorization.RightsScope
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.UserId
import com.junbo.common.id.UserTFABackupCodeId
import com.junbo.common.model.Results
import com.junbo.common.rs.Created201Marker
import com.junbo.identity.auth.UserPropertyAuthorizeCallbackFactory
import com.junbo.identity.core.service.filter.UserTFABackupCodeFilter
import com.junbo.identity.core.service.validator.UserTFABackupCodeValidator
import com.junbo.identity.service.UserTFAPhoneBackupCodeService
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.UserTFABackupCode
import com.junbo.identity.spec.v1.option.list.UserTFABackupCodeListOptions
import com.junbo.identity.spec.v1.option.model.UserTFABackupCodeGetOptions
import com.junbo.identity.spec.v1.resource.UserTFABackupCodeResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired

import javax.transaction.Transactional
import javax.ws.rs.core.Response

/**
 * Created by liangfu on 4/24/14.
 */
@CompileStatic
@Transactional
class UserTFABackupCodeResourceImpl implements UserTFABackupCodeResource {

    @Autowired
    private UserTFAPhoneBackupCodeService userTFAPhoneBackupCodeService

    @Autowired
    private UserTFABackupCodeFilter userTFABackupCodeFilter

    @Autowired
    private UserTFABackupCodeValidator userTFABackupCodeValidator

    @Autowired
    private AuthorizeService authorizeService

    @Autowired
    private UserPropertyAuthorizeCallbackFactory authorizeCallbackFactory

    @Override
    Promise<UserTFABackupCode> create(UserId userId, UserTFABackupCode userTFABackupCode) {
        if (userTFABackupCode == null) {
            throw AppCommonErrors.INSTANCE.requestBodyRequired().exception()
        }

        if (userId == null) {
            throw AppCommonErrors.INSTANCE.parameterRequired('userId').exception()
        }

        def callback = authorizeCallbackFactory.create(userId)
        return RightsScope.with(authorizeService.authorize(callback)) {
            if (!AuthorizeContext.hasRights('create')) {
                throw AppCommonErrors.INSTANCE.forbidden().exception()
            }

            userTFABackupCode = userTFABackupCodeFilter.filterForCreate(userTFABackupCode)

            return userTFABackupCodeValidator.validateForCreate(userId, userTFABackupCode).then {
                return userTFAPhoneBackupCodeService.create(userTFABackupCode).then { UserTFABackupCode newBackupCode ->
                    Created201Marker.mark(newBackupCode.getId())

                    newBackupCode = userTFABackupCodeFilter.filterForGet(newBackupCode, null)
                    return Promise.pure(newBackupCode)
                }
            }
        }
    }

    @Override
    Promise<UserTFABackupCode> get(UserId userId, UserTFABackupCodeId userTFABackupCodeId,
            UserTFABackupCodeGetOptions getOptions) {
        if (getOptions == null) {
            throw new IllegalArgumentException('getOptions is null')
        }

        if (userId == null) {
            throw AppCommonErrors.INSTANCE.parameterRequired('userId').exception()
        }
        if (userTFABackupCodeId == null) {
            throw AppCommonErrors.INSTANCE.parameterRequired('userTFABackupCodeId').exception()
        }

        def callback = authorizeCallbackFactory.create(userId)
        return RightsScope.with(authorizeService.authorize(callback)) {
            if (!AuthorizeContext.hasRights('read')) {
                throw AppErrors.INSTANCE.userNotFound(userId).exception()
            }

            return userTFABackupCodeValidator.validateForGet(userId, userTFABackupCodeId).
                    then { UserTFABackupCode newUserTFABackupCode ->
                newUserTFABackupCode = userTFABackupCodeFilter.filterForGet(newUserTFABackupCode,
                        getOptions.properties?.split(',') as List<String>)

                return Promise.pure(newUserTFABackupCode)
            }
        }
    }

    @Override
    Promise<UserTFABackupCode> patch(UserId userId, UserTFABackupCodeId userTFABackupCodeId,
                                      UserTFABackupCode userTFABackupCode) {
        if (userId == null) {
            throw AppCommonErrors.INSTANCE.parameterRequired('userId').exception()
        }

        if (userTFABackupCodeId == null) {
            throw AppCommonErrors.INSTANCE.parameterRequired('userTFABackupCodeId').exception()
        }

        if (userTFABackupCode == null) {
            throw AppCommonErrors.INSTANCE.requestBodyRequired().exception()
        }

        def callback = authorizeCallbackFactory.create(userId)
        return RightsScope.with(authorizeService.authorize(callback)) {
            if (!AuthorizeContext.hasRights('update')) {
                throw AppCommonErrors.INSTANCE.forbidden().exception()
            }

            return userTFAPhoneBackupCodeService.get(userTFABackupCodeId).then { UserTFABackupCode oldBackupCode ->
                if (oldBackupCode == null) {
                    throw AppErrors.INSTANCE.userTFABackupCodeNotFound(userTFABackupCodeId).exception()
                }

                userTFABackupCode = userTFABackupCodeFilter.filterForPatch(userTFABackupCode, oldBackupCode)

                return userTFABackupCodeValidator.
                        validateForUpdate(userId, userTFABackupCodeId, userTFABackupCode, oldBackupCode).then {

                    return userTFAPhoneBackupCodeService.update(userTFABackupCode, oldBackupCode).then { UserTFABackupCode newCode ->
                        newCode = userTFABackupCodeFilter.filterForGet(newCode, null)
                        return Promise.pure(newCode)
                    }
                }
            }
        }
    }

    @Override
    Promise<UserTFABackupCode> put(UserId userId, UserTFABackupCodeId userTFABackupCodeId,
                                    UserTFABackupCode userTFABackupCode) {
        if (userId == null) {
            throw AppCommonErrors.INSTANCE.parameterRequired('userId').exception()
        }

        if (userTFABackupCodeId == null) {
            throw AppCommonErrors.INSTANCE.parameterRequired('userTFABackupCodeId').exception()
        }

        if (userTFABackupCode == null) {
            throw AppCommonErrors.INSTANCE.requestBodyRequired().exception()
        }

        def callback = authorizeCallbackFactory.create(userId)
        return RightsScope.with(authorizeService.authorize(callback)) {
            if (!AuthorizeContext.hasRights('update')) {
                throw AppCommonErrors.INSTANCE.forbidden().exception()
            }

            return userTFAPhoneBackupCodeService.get(userTFABackupCodeId).then { UserTFABackupCode oldBackupCode ->
                if (oldBackupCode == null) {
                    throw AppErrors.INSTANCE.userTFABackupCodeNotFound(userTFABackupCodeId).exception()
                }

                userTFABackupCode = userTFABackupCodeFilter.filterForPut(userTFABackupCode, oldBackupCode)

                return userTFABackupCodeValidator.
                        validateForUpdate(userId, userTFABackupCodeId, userTFABackupCode, oldBackupCode).then {
                    return userTFAPhoneBackupCodeService.update(userTFABackupCode, oldBackupCode).then { UserTFABackupCode newCode ->
                        newCode = userTFABackupCodeFilter.filterForGet(newCode, null)
                        return Promise.pure(newCode)
                    }
                }
            }
        }
    }

    @Override
    Promise<Void> delete(UserId userId, UserTFABackupCodeId userTFABackupCodeId) {
        if (userId == null) {
            throw AppCommonErrors.INSTANCE.parameterRequired('userId').exception()
        }

        if (userTFABackupCodeId == null) {
            throw AppCommonErrors.INSTANCE.parameterRequired('userTFABackupCodeId').exception()
        }

        def callback = authorizeCallbackFactory.create(userId)
        return RightsScope.with(authorizeService.authorize(callback)) {
            if (!AuthorizeContext.hasRights('delete')) {
                throw AppCommonErrors.INSTANCE.forbidden().exception()
            }

            return userTFABackupCodeValidator.validateForGet(userId, userTFABackupCodeId).then {
                return userTFAPhoneBackupCodeService.delete(userTFABackupCodeId).then {
                    return Promise.pure(Response.status(204).build())
                }
            }
        }
    }

    @Override
    Promise<Results<UserTFABackupCode>> list(UserId userId, UserTFABackupCodeListOptions listOptions) {
        if (userId == null) {
            throw AppCommonErrors.INSTANCE.parameterRequired('userId').exception()
        }

        if (listOptions == null) {
            throw new IllegalArgumentException('listOptions is null')
        }

        def callback = authorizeCallbackFactory.create(userId)
        return RightsScope.with(authorizeService.authorize(callback)) {
            def result = new Results<UserTFABackupCode>(items: [])
            if (!AuthorizeContext.hasRights('read')) {
                return Promise.pure(result)
            }

            return userTFABackupCodeValidator.validateForSearch(userId, listOptions).then {
                return search(listOptions).then { List<UserTFABackupCode> userTFABackupCodeList ->
                    userTFABackupCodeList.each { UserTFABackupCode userTFABackupCode ->
                        if (userTFABackupCode != null) {
                            userTFABackupCode = userTFABackupCodeFilter.filterForGet(userTFABackupCode,
                                    listOptions.properties?.split(',') as List<String>)

                            result.items.add(userTFABackupCode)
                        }
                    }

                    return Promise.pure(result)
                }
            }
        }
    }

    private Promise<List<UserTFABackupCode>> search(UserTFABackupCodeListOptions listOptions) {
        if (listOptions.userId != null && listOptions.active != null) {
            return userTFAPhoneBackupCodeService.searchByUserIdAndActiveStatus(listOptions.userId, listOptions.active,
                    listOptions.limit, listOptions.offset)
        } else if (listOptions.userId != null) {
            return userTFAPhoneBackupCodeService.searchByUserId(listOptions.userId, listOptions.limit, listOptions.offset)
        } else {
            throw AppCommonErrors.INSTANCE.invalidOperation('Unsupported search operation').exception()
        }
    }
}
