package com.junbo.identity.rest.resource.v1

import com.junbo.common.id.Id
import com.junbo.common.id.UserId
import com.junbo.common.id.UserTeleBackupCodeId
import com.junbo.common.model.Results
import com.junbo.common.rs.Created201Marker
import com.junbo.identity.core.service.filter.UserTeleBackupCodeFilter
import com.junbo.identity.core.service.validator.UserTeleBackupCodeValidator
import com.junbo.identity.data.repository.UserTeleBackupCodeRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.UserTeleBackupCode
import com.junbo.identity.spec.v1.option.list.UserTeleBackupCodeListOptions
import com.junbo.identity.spec.v1.option.model.UserTeleBackupCodeGetOptions
import com.junbo.identity.spec.v1.resource.UserTeleBackupCodeResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired

import javax.transaction.Transactional

/**
 * Created by liangfu on 4/24/14.
 */
@CompileStatic
@Transactional
class UserTeleBackupCodeResourceImpl implements UserTeleBackupCodeResource {

    @Autowired
    private UserTeleBackupCodeRepository userTeleBackupCodeRepository

    @Autowired
    private UserTeleBackupCodeFilter userTeleBackupCodeFilter

    @Autowired
    private UserTeleBackupCodeValidator userTeleBackupCodeValidator

    @Override
    Promise<UserTeleBackupCode> create(UserId userId, UserTeleBackupCode userTeleBackupCode) {
        if (userTeleBackupCode == null) {
            throw new IllegalArgumentException('userTeleBackupCode is null')
        }

        userTeleBackupCode = userTeleBackupCodeFilter.filterForCreate(userTeleBackupCode)

        return userTeleBackupCodeValidator.validateForCreate(userId, userTeleBackupCode).then {
            return userTeleBackupCodeRepository.create(userTeleBackupCode).then { UserTeleBackupCode newBackupCode ->
                Created201Marker.mark((Id)newBackupCode.id)

                newBackupCode = userTeleBackupCodeFilter.filterForGet(newBackupCode, null)
                return Promise.pure(newBackupCode)
            }
        }
    }

    @Override
    Promise<UserTeleBackupCode> get(UserId userId, UserTeleBackupCodeId userTeleBackupCodeId,
            UserTeleBackupCodeGetOptions getOptions) {
        if (getOptions == null) {
            throw new IllegalArgumentException('getOptions is null')
        }

        return userTeleBackupCodeValidator.validateForGet(userId, userTeleBackupCodeId).
                then { UserTeleBackupCode newUserTeleBackupCode ->
                    newUserTeleBackupCode = userTeleBackupCodeFilter.filterForGet(newUserTeleBackupCode,
                        getOptions.properties?.split(',') as List<String>)

                    return Promise.pure(newUserTeleBackupCode)
        }
    }

    @Override
    Promise<UserTeleBackupCode> patch(UserId userId, UserTeleBackupCodeId userTeleBackupCodeId,
                                      UserTeleBackupCode userTeleBackupCode) {
        if (userId == null) {
            throw new IllegalArgumentException('userId is null')
        }

        if (userTeleBackupCodeId == null) {
            throw new IllegalArgumentException('userTeleBackupCodeId is null')
        }

        if (userTeleBackupCode == null) {
            throw new IllegalArgumentException('userTeleBackupCode is null')
        }

        return userTeleBackupCodeRepository.get(userTeleBackupCodeId).then { UserTeleBackupCode oldBackupCode ->
            if (oldBackupCode == null) {
                throw AppErrors.INSTANCE.userTeleBackupCodeNotFound(userTeleBackupCodeId).exception()
            }

            userTeleBackupCode = userTeleBackupCodeFilter.filterForPatch(userTeleBackupCode, oldBackupCode)

            return userTeleBackupCodeValidator.
                    validateForUpdate(userId, userTeleBackupCodeId, userTeleBackupCode, oldBackupCode).then {

                return userTeleBackupCodeRepository.update(userTeleBackupCode).then { UserTeleBackupCode newCode ->
                    newCode = userTeleBackupCodeFilter.filterForGet(newCode, null)
                    return Promise.pure(newCode)
                }
            }
        }
    }

    @Override
    Promise<UserTeleBackupCode> put(UserId userId, UserTeleBackupCodeId userTeleBackupCodeId,
                                    UserTeleBackupCode userTeleBackupCode) {
        if (userId == null) {
            throw new IllegalArgumentException('userId is null')
        }

        if (userTeleBackupCodeId == null) {
            throw new IllegalArgumentException('userTeleBackupCodeId is null')
        }

        if (userTeleBackupCode == null) {
            throw new IllegalArgumentException('userTeleBackupCode is null')
        }

        return userTeleBackupCodeRepository.get(userTeleBackupCodeId).then { UserTeleBackupCode oldBackupCode ->
            if (oldBackupCode == null) {
                throw AppErrors.INSTANCE.userTeleBackupCodeNotFound(userTeleBackupCodeId).exception()
            }

            userTeleBackupCode = userTeleBackupCodeFilter.filterForPut(userTeleBackupCode, oldBackupCode)

            return userTeleBackupCodeValidator.
                    validateForUpdate(userId, userTeleBackupCodeId, userTeleBackupCode, oldBackupCode).then {
                return userTeleBackupCodeRepository.update(userTeleBackupCode).then { UserTeleBackupCode newCode ->
                    newCode = userTeleBackupCodeFilter.filterForGet(newCode, null)
                    return Promise.pure(newCode)
                }
            }
        }
    }

    @Override
    Promise<Void> delete(UserId userId, UserTeleBackupCodeId userTeleBackupCodeId) {
        return userTeleBackupCodeValidator.validateForGet(userId, userTeleBackupCodeId).then {
            return userTeleBackupCodeRepository.delete(userTeleBackupCodeId)
        }
    }

    @Override
    Promise<Results<UserTeleBackupCode>> list(UserId userId, UserTeleBackupCodeListOptions listOptions) {
        return userTeleBackupCodeValidator.validateForSearch(userId, listOptions).then {
            return userTeleBackupCodeRepository.search(listOptions).then {
                List<UserTeleBackupCode> userTeleBackupCodeList ->
                    def result = new Results<UserTeleBackupCode>(items: [])

                    userTeleBackupCodeList.each { UserTeleBackupCode newUserTeleBackupCode ->
                        if (newUserTeleBackupCode != null) {
                            newUserTeleBackupCode = userTeleBackupCodeFilter.filterForGet(newUserTeleBackupCode,
                                    listOptions.properties?.split(',') as List<String>)

                            result.items.add(newUserTeleBackupCode)
                        }
                    }

                    return Promise.pure(result)
            }
        }
    }
}
