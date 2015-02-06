package com.junbo.identity.service.impl

import com.junbo.common.id.UserAttributeDefinitionId
import com.junbo.common.id.UserAttributeId
import com.junbo.common.id.UserId
import com.junbo.common.model.Results
import com.junbo.identity.data.repository.UserAttributeRepository
import com.junbo.identity.service.UserAttributeService
import com.junbo.identity.spec.v1.model.UserAttribute
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by xiali_000 on 2014/12/19.
 */
@CompileStatic
class UserAttributeServiceImpl implements UserAttributeService {

    UserAttributeRepository userAttributeRepository

    @Override
    Promise<UserAttribute> get(UserAttributeId userAttributeId) {
        return userAttributeRepository.get(userAttributeId).then { UserAttribute userAttribute ->
            return Promise.pure(filter(userAttribute))
        }
    }

    @Override
    Promise<UserAttribute> update(UserAttribute userAttribute, UserAttribute oldUserAttribute) {
        return userAttributeRepository.update(userAttribute, oldUserAttribute).then { UserAttribute updated ->
            return Promise.pure(filter(updated))
        }
    }

    @Override
    Promise<UserAttribute> create(UserAttribute userAttribute) {
        return userAttributeRepository.create(userAttribute).then { UserAttribute created ->
            return Promise.pure(filter(created))
        }
    }

    @Override
    Promise<Void> delete(UserAttributeId userAttributeId) {
        return userAttributeRepository.delete(userAttributeId)
    }

    @Override
    Promise<Results<UserAttribute>> searchByUserId(UserId userId, Integer limit, Integer offset) {
        Results<UserAttribute> results = new Results<>()
        return userAttributeRepository.searchByUserId(userId, limit, offset).then { List<UserAttribute> userAttributeList ->
            results.items = userAttributeList
            results.items.each { UserAttribute existing ->
                filter(existing)
            }
            // todo:    Need to adapt Main branch's repository search
            return Promise.pure(results)
        }
    }

    @Override
    Promise<Results<UserAttribute>> searchByUserAttributeDefinitionId(UserAttributeDefinitionId userAttributeDefinitionId,
                                                                      Integer limit, Integer offset) {
        Results<UserAttribute> results = new Results<>()
        return userAttributeRepository.searchByUserAttributeDefinitionId(userAttributeDefinitionId, limit, offset).then {
            List<UserAttribute> userAttributeList ->
                results.items = userAttributeList
                results.items.each { UserAttribute existing ->
                    filter(existing)
                }
                return Promise.pure(results)
        }
    }

    @Override
    Promise<Results<UserAttribute>> searchByUserIdAndAttributeDefinitionId(UserId userId,
                               UserAttributeDefinitionId userAttributeDefinitionId, Integer limit, Integer offset) {
        Results<UserAttribute> results = new Results<>()
        return userAttributeRepository.searchByUserIdAndAttributeDefinitionId(userId, userAttributeDefinitionId, limit, offset).then {
            List<UserAttribute> userAttributeList ->
                results.items = userAttributeList
                results.items.each { UserAttribute existing ->
                    filter(existing)
                }

                return Promise.pure(results)
        }
    }

    @Override
    Promise<Results<UserAttribute>> searchByActive(Boolean active, Integer limit, Integer offset) {
        Results<UserAttribute> results = new Results<>()
        return userAttributeRepository.searchByActive(active, limit, offset).then { List<UserAttribute> userAttributeList ->
            results.items = userAttributeList
            results.items.each { UserAttribute existing ->
                filter(existing)
            }

            return Promise.pure(results)
        }
    }

    @Required
    void setUserAttributeRepository(UserAttributeRepository userAttributeRepository) {
        this.userAttributeRepository = userAttributeRepository
    }

    private static UserAttribute filter(UserAttribute userAttribute) {
        if (userAttribute == null) {
            return userAttribute
        }

        if ((userAttribute.isSuspended != null && userAttribute.isSuspended)
          || (userAttribute.useCount != null && userAttribute.useCount == 0)
          || (userAttribute.expirationTime != null && userAttribute.expirationTime.before(new Date()))) {
            userAttribute.setIsActive(false)
            return userAttribute
        }

        userAttribute.setIsActive(true)
        return userAttribute
    }
}
