package com.junbo.identity.rest.resource.v1

import com.junbo.common.id.Id
import com.junbo.common.id.UserId
import com.junbo.common.model.Results
import com.junbo.identity.core.service.Created201Marker
import com.junbo.identity.core.service.filter.UserFilter
import com.junbo.identity.core.service.normalize.NormalizeService
import com.junbo.identity.core.service.validator.UserValidator
import com.junbo.identity.data.repository.UserGroupRepository
import com.junbo.identity.data.repository.UserRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.model.UserGroup
import com.junbo.identity.spec.v1.option.list.UserGroupListOptions
import com.junbo.identity.spec.v1.option.list.UserListOptions
import com.junbo.identity.spec.v1.option.model.UserGetOptions
import com.junbo.identity.spec.v1.resource.UserResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Created by liangfu on 4/10/14.
 */
@Transactional
@CompileStatic
class UserResourceImpl implements UserResource {

    @Autowired
    private Created201Marker created201Marker

    @Autowired
    private UserRepository userRepository

    @Autowired
    private UserGroupRepository userGroupRepository

    @Autowired
    private UserValidator userValidator

    @Autowired
    private UserFilter userFilter

    @Autowired
    private NormalizeService normalizeService

    @Override
    Promise<User> create(User user) {
        if (user == null) {
            throw new IllegalArgumentException('user is null')
        }

        user = userFilter.filterForCreate(user)

        return userValidator.validateForCreate(user).then {
            userRepository.create(user).then { User newUser ->
                created201Marker.mark((Id) newUser.id)

                newUser = userFilter.filterForGet(newUser, null)
                return Promise.pure(newUser)
            }
        }
    }

    @Override
    Promise<User> put(UserId userId, User user) {
        if (userId == null) {
            throw new IllegalArgumentException('userId is null')
        }

        if (user == null) {
            throw new IllegalArgumentException('user is null')
        }

        return userRepository.get(userId).then { User oldUser ->
            if (oldUser == null) {
                throw AppErrors.INSTANCE.userNotFound(userId).exception()
            }

            user = userFilter.filterForPut(user, oldUser)

            return userValidator.validateForUpdate(user, oldUser).then {
                userRepository.update(user).then { User newUser ->
                    newUser = userFilter.filterForGet(newUser, null)
                    return Promise.pure(newUser)
                }
            }
        }
    }

    @Override
    Promise<User> patch(UserId userId, User user) {
        if (userId == null) {
            throw new IllegalArgumentException('userId is null')
        }

        if (user == null) {
            throw new IllegalArgumentException('user is null')
        }

        return userRepository.get(userId).then { User oldUser ->
            if (oldUser == null) {
                throw AppErrors.INSTANCE.userNotFound(userId).exception()
            }

            user = userFilter.filterForPatch(user, oldUser)

            return userValidator.validateForUpdate(user, oldUser).then {
                return userRepository.update(user).then { User newUser ->
                    newUser = userFilter.filterForGet(newUser, null)
                    return Promise.pure(newUser)
                }
            }
        }
    }

    @Override
    Promise<User> get(UserId userId, UserGetOptions getOptions) {
        if (userId == null) {
            throw new IllegalArgumentException('userId is null')
        }

        if (getOptions == null) {
            throw new IllegalArgumentException('getOptions is null')
        }

        return userValidator.validateForGet(userId).then { User user ->
            user = userFilter.filterForGet(user, getOptions.properties?.split(',') as List<String>)

            return Promise.pure(user)
        }
    }

    @Override
    Promise<Results<User>> list(UserListOptions listOptions) {
        return userValidator.validateForSearch(listOptions).then {
            def resultList = new Results<User>(items: [])

            if (listOptions.username != null) {
                String canonicalUsername = normalizeService.normalize(listOptions.username)
                return userRepository.getUserByCanonicalUsername(canonicalUsername).then { User user ->
                    if (user != null) {
                        user = userFilter.filterForGet(user, listOptions.properties?.split(',') as List<String>)
                        resultList.items.add(user)
                    }

                    return Promise.pure(resultList)
                }
            } else {
                return userGroupRepository.search(new UserGroupListOptions(
                    groupId: listOptions.groupId
                )).then { List<UserGroup> userGroupList ->
                    return fillUserGroups(userGroupList.iterator(), resultList, listOptions).then {
                        return Promise.pure(resultList)
                    }
                }
            }
        }
    }

    private Promise<Void> fillUserGroups(Iterator<UserGroup> it, Results<User> resultList,
                                         UserListOptions listOptions) {
        if (it.hasNext()) {
            UserGroup userGroup = it.next()
            return userValidator.validateForGet(userGroup.userId).then { User existing ->
                existing = userFilter.filterForGet(existing, listOptions.properties?.split(',') as List<String>)
                resultList.items.add(existing)

                return fillUserGroups(it, resultList, listOptions)
            }
        }

        return Promise.pure(null)
    }

    @Override
    Promise<Void> delete(UserId userId) {
        if (userId == null) {
            throw new IllegalArgumentException('userId is null')
        }

        return userValidator.validateForGet(userId).then {
            userRepository.delete(userId)

            return Promise.pure(null)
        }
    }
}
