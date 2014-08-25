package com.junbo.identity.rest.resource.v1

import com.junbo.authorization.AuthorizeContext
import com.junbo.authorization.AuthorizeService
import com.junbo.authorization.RightsScope
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.UserGroupId
import com.junbo.common.model.Results
import com.junbo.common.rs.Created201Marker
import com.junbo.identity.auth.GroupAuthorizeCallbackFactory
import com.junbo.identity.core.service.filter.UserGroupFilter
import com.junbo.identity.core.service.validator.UserGroupValidator
import com.junbo.identity.data.repository.UserGroupRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.UserGroup
import com.junbo.identity.spec.v1.option.list.UserGroupListOptions
import com.junbo.identity.spec.v1.option.model.UserGroupGetOptions
import com.junbo.identity.spec.v1.resource.UserGroupMembershipResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Created by liangfu on 4/9/14.
 */
@Transactional
@CompileStatic
class UserGroupMembershipResourceImpl implements UserGroupMembershipResource {

    @Autowired
    private UserGroupRepository userGroupRepository

    @Autowired
    private UserGroupFilter userGroupFilter

    @Autowired
    private UserGroupValidator userGroupValidator

    @Autowired
    private AuthorizeService authorizeService

    @Autowired
    private GroupAuthorizeCallbackFactory authorizeCallbackFactory

    @Override
    Promise<UserGroup> create(UserGroup userGroup) {
        if (userGroup == null) {
            throw new IllegalArgumentException('userGroup is null')
        }

        userGroup = userGroupFilter.filterForCreate(userGroup)

        return userGroupValidator.validateForCreate(userGroup).then {
            def callback = authorizeCallbackFactory.create(userGroup.groupId)
            return RightsScope.with(authorizeService.authorize(callback)) {
                if (!AuthorizeContext.hasRights('create')) {
                    throw AppCommonErrors.INSTANCE.forbidden().exception()
                }

                return userGroupRepository.create(userGroup).then { UserGroup newUserGroup ->
                    Created201Marker.mark(newUserGroup.getId())

                    newUserGroup = userGroupFilter.filterForGet(newUserGroup, null)
                    return Promise.pure(newUserGroup)
                }
            }
        }
    }

    @Override
    Promise<UserGroup> get(UserGroupId userGroupId, UserGroupGetOptions getOptions) {
        if (getOptions == null) {
            throw new IllegalArgumentException('getOptions is null')
        }

        return userGroupValidator.validateForGet(userGroupId).then { UserGroup newUserGroup ->
            def callback = authorizeCallbackFactory.create(newUserGroup.groupId)
            return RightsScope.with(authorizeService.authorize(callback)) {
                if (!AuthorizeContext.hasRights('read')) {
                    throw AppErrors.INSTANCE.userGroupNotFound(userGroupId).exception()
                }

                newUserGroup = userGroupFilter.filterForGet(newUserGroup,
                        getOptions.properties?.split(',') as List<String>)

                return Promise.pure(newUserGroup)
            }
        }
    }

    @Override
    Promise<UserGroup> patch(UserGroupId userGroupId, UserGroup userGroup) {
        if (userGroupId == null) {
            throw new IllegalArgumentException('userGroupId is null')
        }

        if (userGroup == null) {
            throw new IllegalArgumentException('userGroup is null')
        }

        return userGroupRepository.get(userGroupId).then { UserGroup oldUserGroup ->
            if (oldUserGroup == null) {
                throw AppErrors.INSTANCE.userGroupNotFound(userGroupId).exception()
            }

            def callback = authorizeCallbackFactory.create(oldUserGroup.groupId)
            return RightsScope.with(authorizeService.authorize(callback)) {
                if (!AuthorizeContext.hasRights('update')) {
                    throw AppCommonErrors.INSTANCE.forbidden().exception()
                }

                userGroup = userGroupFilter.filterForPatch(userGroup, oldUserGroup)

                return userGroupValidator.validateForUpdate(userGroupId, userGroup, oldUserGroup).then {
                    return userGroupRepository.update(userGroup, oldUserGroup).then { UserGroup newUserGroup ->
                        newUserGroup = userGroupFilter.filterForGet(newUserGroup, null)
                        return Promise.pure(newUserGroup)
                    }
                }
            }
        }
    }

    @Override
    Promise<UserGroup> put(UserGroupId userGroupId, UserGroup userGroup) {
        if (userGroupId == null) {
            throw new IllegalArgumentException('userGroupId is null')
        }

        if (userGroup == null) {
            throw new IllegalArgumentException('userGroup is null')
        }

        return userGroupRepository.get(userGroupId).then { UserGroup oldUserGroup ->
            if (oldUserGroup == null) {
                throw AppErrors.INSTANCE.userGroupNotFound(userGroupId).exception()
            }

            def callback = authorizeCallbackFactory.create(oldUserGroup.groupId)
            return RightsScope.with(authorizeService.authorize(callback)) {
                if (!AuthorizeContext.hasRights('update')) {
                    throw AppCommonErrors.INSTANCE.forbidden().exception()
                }

                userGroup = userGroupFilter.filterForPut(userGroup, oldUserGroup)

                return userGroupValidator.validateForUpdate(userGroupId, userGroup, oldUserGroup).then {
                    return userGroupRepository.update(userGroup, oldUserGroup).then { UserGroup newUserGroup ->
                        newUserGroup = userGroupFilter.filterForGet(newUserGroup, null)
                        return Promise.pure(newUserGroup)
                    }
                }
            }
        }
    }

    @Override
    Promise<Void> delete(UserGroupId userGroupId) {
        return userGroupValidator.validateForGet(userGroupId).then { UserGroup userGroup ->
            def callback = authorizeCallbackFactory.create(userGroup.groupId)
            return RightsScope.with(authorizeService.authorize(callback)) {
                if (!AuthorizeContext.hasRights('delete') && AuthorizeContext.currentUserId != userGroup.userId) {
                    throw AppCommonErrors.INSTANCE.forbidden().exception()
                }
                return userGroupRepository.delete(userGroupId)
            }

        }
    }

    @Override
    Promise<Results<UserGroup>> list(UserGroupListOptions listOptions) {
        if (listOptions == null) {
            throw new IllegalArgumentException('listOptions is null')
        }

        return userGroupValidator.validateForSearch(listOptions).then {
            return search(listOptions).then { List<UserGroup> userGroupList ->
                def result = new Results<UserGroup>(items: [])

                return Promise.each(userGroupList) { UserGroup newUserGroup ->
                    if (newUserGroup != null) {
                        newUserGroup = userGroupFilter.filterForGet(newUserGroup,
                                listOptions.properties?.split(',') as List<String>)
                    }

                    if (newUserGroup != null) {
                        def callback = authorizeCallbackFactory.create(newUserGroup.groupId)
                        return RightsScope.with(authorizeService.authorize(callback)) {
                            if (AuthorizeContext.hasRights('read')) {
                                result.items.add(newUserGroup)
                                return Promise.pure(newUserGroup)
                            } else {
                                return Promise.pure(null)
                            }
                        }
                    }

                    return Promise.pure(null)
                }.then {
                    return Promise.pure(result)
                }
            }
        }
    }

    private Promise<List<UserGroup>> search(UserGroupListOptions listOptions) {
        if (listOptions.userId != null && listOptions.groupId != null) {
            return userGroupRepository.searchByUserIdAndGroupId(listOptions.userId, listOptions.groupId,
                    listOptions.limit, listOptions.offset)
        } else if (listOptions.userId != null) {
            return userGroupRepository.searchByUserId(listOptions.userId, listOptions.limit, listOptions.offset)
        } else if (listOptions.groupId != null) {
            return userGroupRepository.searchByGroupId(listOptions.groupId, listOptions.limit, listOptions.offset)
        } else {
            throw new IllegalArgumentException('Unsupported search operation.')
        }
    }
}
