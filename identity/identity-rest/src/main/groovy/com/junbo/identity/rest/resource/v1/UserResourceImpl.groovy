package com.junbo.identity.rest.resource.v1

import com.junbo.authorization.AuthorizeContext
import com.junbo.authorization.AuthorizeService
import com.junbo.authorization.RightsScope
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.UserId
import com.junbo.common.model.Results
import com.junbo.common.rs.Created201Marker
import com.junbo.csr.spec.def.CsrLogActionType
import com.junbo.csr.spec.model.CsrLog
import com.junbo.csr.spec.resource.CsrLogResource
import com.junbo.identity.auth.UserAuthorizeCallbackFactory
import com.junbo.identity.core.service.filter.UserFilter
import com.junbo.identity.core.service.normalize.NormalizeService
import com.junbo.identity.core.service.validator.UserValidator
import com.junbo.identity.data.repository.UserGroupRepository
import com.junbo.identity.data.repository.UserPasswordRepository
import com.junbo.identity.data.repository.UserPinRepository
import com.junbo.identity.data.repository.UserRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.model.users.UserPassword
import com.junbo.identity.spec.model.users.UserPin
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.model.UserGroup
import com.junbo.identity.spec.v1.option.list.UserListOptions
import com.junbo.identity.spec.v1.option.model.UserGetOptions
import com.junbo.identity.spec.v1.resource.UserResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.CollectionUtils

/**
 * Created by liangfu on 4/10/14.
 */
@Transactional
@CompileStatic
class UserResourceImpl implements UserResource {

    @Autowired
    private UserRepository userRepository

    @Autowired
    private UserGroupRepository userGroupRepository

    @Autowired
    private UserPasswordRepository userPasswordRepository

    @Autowired
    private UserPinRepository userPinRepository

    @Autowired
    private UserValidator userValidator

    @Autowired
    private UserFilter userFilter

    @Autowired
    private NormalizeService normalizeService

    @Autowired
    private AuthorizeService authorizeService

    @Autowired
    private UserAuthorizeCallbackFactory userAuthorizeCallbackFactory

    @Autowired
    @Qualifier('identityCsrLogResource')
    private CsrLogResource csrLogResource

    @Override
    Promise<User> create(User user) {
        if (user == null) {
            throw AppCommonErrors.INSTANCE.requestBodyRequired().exception()
        }

        def callback = userAuthorizeCallbackFactory.create(user)
        return RightsScope.with(authorizeService.authorize(callback)) {
            if (!AuthorizeContext.hasRights('create')) {
                throw AppCommonErrors.INSTANCE.forbidden().exception()
            }

            user = userFilter.filterForCreate(user)

            return userValidator.validateForCreate(user).then {
                return userRepository.create(user).then { User newUser ->
                    Created201Marker.mark(newUser.getId())

                    newUser = userFilter.filterForGet(newUser, null)
                    return Promise.pure(newUser)
                }
            }
        }
    }

    @Override
    Promise<User> put(UserId userId, User user) {
        if (userId == null) {
            throw new IllegalArgumentException('userId is null')
        }

        if (user == null) {
            throw AppCommonErrors.INSTANCE.requestBodyRequired().exception()
        }

        return userRepository.get(userId).then { User oldUser ->
            String oldCanonicalUsername = oldUser.canonicalUsername
            if (oldUser == null) {
                throw AppErrors.INSTANCE.userNotFound(userId).exception()
            }

            def callback = userAuthorizeCallbackFactory.create(oldUser)
            return RightsScope.with(authorizeService.authorize(callback)) {
                if (!AuthorizeContext.hasRights('update')) {
                    throw AppCommonErrors.INSTANCE.forbidden().exception()
                }

                if (AuthorizeContext.hasScopes('csr') && AuthorizeContext.currentUserId != null) {
                    csrLogResource.create(new CsrLog(userId: AuthorizeContext.currentUserId, regarding: 'Account', action: CsrLogActionType.CountryUpdated, property: oldUser.username)).get()
                }

                user = userFilter.filterForPut(user, oldUser)

                return userValidator.validateForUpdate(user, oldUser).then {
                    return userRepository.update(user, oldUser).then { User newUser ->
                        String newCanonicalUsername = newUser.canonicalUsername
                        return updateCredential(user.getId(), oldCanonicalUsername, newCanonicalUsername).then {
                            newUser = userFilter.filterForGet(newUser, null)
                            return Promise.pure(newUser)
                        }
                    }
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
            String oldCanonicalUsername = oldUser.canonicalUsername
            if (oldUser == null) {
                throw AppErrors.INSTANCE.userNotFound(userId).exception()
            }

            def callback = userAuthorizeCallbackFactory.create(oldUser)
            return RightsScope.with(authorizeService.authorize(callback)) {
                if (!AuthorizeContext.hasRights('update')) {
                    throw AppCommonErrors.INSTANCE.forbidden().exception()
                }

                user = userFilter.filterForPatch(user, oldUser)

                return userValidator.validateForUpdate(user, oldUser).then {
                    return userRepository.update(user, oldUser).then { User newUser ->
                        String newCanonicalUsername = newUser.canonicalUsername
                        return updateCredential(user.getId(), oldCanonicalUsername, newCanonicalUsername).then {
                            newUser = userFilter.filterForGet(newUser, null)
                            return Promise.pure(newUser)
                        }
                    }
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

            def callback = userAuthorizeCallbackFactory.create(user)
            return RightsScope.with(authorizeService.authorize(callback)) {
                if (!AuthorizeContext.hasRights('read')) {
                    throw AppErrors.INSTANCE.userNotFound(userId).exception()
                }

                user = userFilter.filterForGet(user, getOptions.properties?.split(',') as List<String>)

                if (user == null) {
                    throw AppErrors.INSTANCE.userNotFound(userId).exception()
                }

                return Promise.pure(user)
            }
        }
    }

    @Override
    Promise<Results<User>> list(UserListOptions listOptions) {
        return userValidator.validateForSearch(listOptions).then {
            def resultList = new Results<User>(items: [])

            def filterUser = { User user ->
                def callback = userAuthorizeCallbackFactory.create(user)
                return RightsScope.with(authorizeService.authorize(callback)) {
                    if (!AuthorizeContext.hasRights('search')) {
                        throw AppCommonErrors.INSTANCE.forbidden().exception()
                    }

                    user = userFilter.filterForGet(user, listOptions.properties?.split(',') as List<String>)

                    if (user != null) {
                        resultList.items.add(user)
                        return Promise.pure(user)
                    } else {
                        return Promise.pure(null)
                    }
                }
            }

            if (listOptions.username != null) {
                String canonicalUsername = normalizeService.normalize(listOptions.username)
                return userRepository.searchUserByCanonicalUsername(canonicalUsername).then { User user ->
                    if (user == null) {
                        return Promise.pure(resultList)
                    }

                    return filterUser(user).then {
                        return Promise.pure(resultList)
                    }
                }
            } else {
                return userGroupRepository.searchByGroupId(listOptions.groupId, listOptions.limit,
                        listOptions.offset).then { List<UserGroup> userGroupList ->
                    return Promise.each(userGroupList) { UserGroup userGroup ->
                        return userValidator.validateForGet(userGroup.userId).then(filterUser)
                    }.then {
                        return Promise.pure(resultList)
                    }
                }
            }
        }
    }

    @Override
    Promise<Void> delete(UserId userId) {
        if (userId == null) {
            throw new IllegalArgumentException('userId is null')
        }

        return userValidator.validateForGet(userId).then { User user ->
            def callback = userAuthorizeCallbackFactory.create(user)
            return RightsScope.with(authorizeService.authorize(callback)) {
                if (!AuthorizeContext.hasRights('delete')) {
                    throw AppCommonErrors.INSTANCE.forbidden().exception()
                }

                return userRepository.delete(userId)
            }
        }
    }

    Promise<Void> updateCredential(UserId userId, String oldCanonicalUsername, String newCanonicalUsername) {
        if (oldCanonicalUsername == newCanonicalUsername) {
            return Promise.pure(null)
        } else {
            // if username changes, will disable all passwords and pins. User needs to reset this.
            return userPasswordRepository.searchByUserIdAndActiveStatus(userId, true, Integer.MAX_VALUE, 0).then {
                List<UserPassword> userPasswordList ->
                    if (CollectionUtils.isEmpty(userPasswordList)) {
                        return Promise.pure(null)
                    }

                    return Promise.each(userPasswordList.iterator()) { UserPassword userPassword ->
                        userPassword.active = false
                        return userPasswordRepository.update(userPassword, userPassword)
                    }.then {
                        return Promise.pure(null)
                    }
            }.then {
                return userPinRepository.searchByUserIdAndActiveStatus(userId, true, Integer.MAX_VALUE, 0).then {
                    List<UserPin> userPinList ->
                        if (CollectionUtils.isEmpty(userPinList)) {
                            return Promise.pure(null)
                        }

                        return Promise.each(userPinList.iterator()) { UserPin userPin ->
                            userPin.active = false
                            return userPinRepository.update(userPin, userPin)
                        }.then {
                            return Promise.pure(null)
                        }
                }
            }
        }
    }
}
