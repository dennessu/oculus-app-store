package com.junbo.identity.rest.resource.v1

import com.junbo.common.id.Id
import com.junbo.common.id.UserId
import com.junbo.common.model.Results
import com.junbo.identity.core.service.Created201Marker
import com.junbo.identity.core.service.filter.UserFilter
import com.junbo.identity.core.service.normalize.NormalizeService
import com.junbo.identity.core.service.validator.UserValidator
import com.junbo.identity.data.repository.UserRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.option.list.UserListOptions
import com.junbo.identity.spec.v1.option.model.UserGetOptions
import com.junbo.identity.spec.v1.resource.UserResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.StringUtils

/**
 * Created by liangfu on 4/10/14.
 */
@Transactional
@CompileStatic
class UserResourceImpl implements UserResource {

    @Autowired
    private Created201Marker created201Marker

    @Autowired
    @Qualifier('userRepository')
    private UserRepository userRepository

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

        userValidator.validateForCreate(user).then {
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

        userRepository.get(userId).then { User oldUser ->
            if (oldUser == null) {
                throw AppErrors.INSTANCE.userNotFound(userId).exception()
            }

            user = userFilter.filterForPut(user, oldUser)

            userValidator.validateForUpdate(user, oldUser).then {
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

        userRepository.get(userId).then { User oldUser ->
            if (oldUser == null) {
                throw AppErrors.INSTANCE.userNotFound(userId).exception()
            }

            user = userFilter.filterForPatch(user, oldUser)

            userValidator.validateForUpdate(user, oldUser).then {
                userRepository.update(user).then { User newUser ->
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

        userRepository.get(userId).then { User user ->
            if (user == null) {
                throw AppErrors.INSTANCE.userNotFound(userId).exception()
            }

            user = userFilter.filterForGet(user, getOptions.properties?.split(',') as List<String>)

            return Promise.pure(user)
        }
    }

    @Override
    Promise<Results<User>> list(UserListOptions listOptions) {
        if (listOptions == null) {
            throw new IllegalArgumentException('listOptions is null')
        }

        if (StringUtils.isEmpty(listOptions.username)) {
            throw AppErrors.INSTANCE.fieldRequired('username').exception()
        }

        String canonicalUsername = normalizeService.normalize(listOptions.username)
        userRepository.getUserByCanonicalUsername(canonicalUsername).then { User user ->
            def resultList = new Results<User>(items: [])

            if (user != null) {
                user = userFilter.filterForGet(user, listOptions.properties?.split(',') as List<String>)
            }

            if (user != null) {
                resultList.items.add(user)
            }

            return Promise.pure(resultList)
        }
    }
}
