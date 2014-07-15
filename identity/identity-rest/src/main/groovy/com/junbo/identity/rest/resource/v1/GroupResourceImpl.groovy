/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.resource.v1
import com.junbo.authorization.AuthorizeContext
import com.junbo.authorization.AuthorizeService
import com.junbo.authorization.RightsScope
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.GroupId
import com.junbo.common.model.Results
import com.junbo.common.rs.Created201Marker
import com.junbo.identity.auth.GroupAuthorizeCallbackFactory
import com.junbo.identity.core.service.filter.GroupFilter
import com.junbo.identity.core.service.validator.GroupValidator
import com.junbo.identity.data.repository.GroupRepository
import com.junbo.identity.data.repository.UserGroupRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.Group
import com.junbo.identity.spec.v1.model.UserGroup
import com.junbo.identity.spec.v1.option.list.GroupListOptions
import com.junbo.identity.spec.v1.option.model.GroupGetOptions
import com.junbo.identity.spec.v1.resource.GroupResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.apache.commons.collections.CollectionUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
/**
 * Created by xiali_000 on 4/8/2014.
 */
@Transactional
@CompileStatic
class GroupResourceImpl implements GroupResource {

    @Autowired
    private GroupRepository groupRepository

    @Autowired
    private UserGroupRepository userGroupRepository

    @Autowired
    private GroupFilter groupFilter

    @Autowired
    private GroupValidator groupValidator

    @Autowired
    private AuthorizeService authorizeService

    @Autowired
    private GroupAuthorizeCallbackFactory authorizeCallbackFactory

    @Override
    Promise<Group> create(Group group) {
        group = groupFilter.filterForCreate(group)

        return groupValidator.validateForCreate(group).then {
            def callback = authorizeCallbackFactory.create(group)
            return RightsScope.with(authorizeService.authorize(callback)) {
                if (!AuthorizeContext.hasRights('create')) {
                    throw AppCommonErrors.INSTANCE.forbidden().exception()
                }

                return groupRepository.create(group).then { Group newGroup ->
                    Created201Marker.mark(newGroup.getId())

                    newGroup = groupFilter.filterForGet(newGroup, null)
                    return Promise.pure(newGroup)
                }
            }
        }
    }

    @Override
    Promise<Group> put(GroupId groupId, Group group) {

        return groupValidator.validateForGet(groupId).then { Group oldGroup ->
            if (oldGroup == null) {
                throw AppErrors.INSTANCE.groupNotFound(groupId).exception()
            }

            def callback = authorizeCallbackFactory.create(oldGroup)
            return RightsScope.with(authorizeService.authorize(callback)) {
                if (!AuthorizeContext.hasRights('update')) {
                    throw AppCommonErrors.INSTANCE.forbidden().exception()
                }

                group = groupFilter.filterForPut(group, oldGroup)

                return groupValidator.validateForUpdate(groupId, group, oldGroup).then {
                    return groupRepository.update(group).then { Group newGroup ->
                        newGroup = groupFilter.filterForGet(newGroup, null)
                        return Promise.pure(newGroup)
                    }
                }
            }
        }
    }

    @Override
    Promise<Group> patch(GroupId groupId, Group group) {

        return groupValidator.validateForGet(groupId).then { Group oldGroup ->
            if (oldGroup == null) {
                throw AppErrors.INSTANCE.groupNotFound(groupId).exception()
            }

            def callback = authorizeCallbackFactory.create(oldGroup)
            return RightsScope.with(authorizeService.authorize(callback)) {
                if (!AuthorizeContext.hasRights('update')) {
                    throw AppCommonErrors.INSTANCE.forbidden().exception()
                }

                group = groupFilter.filterForPatch(group, oldGroup)

                return groupValidator.validateForUpdate(groupId, group, oldGroup).then {
                    return groupRepository.update(group).then { Group newGroup ->
                        newGroup = groupFilter.filterForGet(newGroup, null)
                        return Promise.pure(newGroup)
                    }
                }
            }
        }
    }

    @Override
    Promise<Group> get(GroupId groupId, GroupGetOptions getOptions) {
        if (getOptions == null) {
            throw new IllegalArgumentException('getOptions is null')
        }

        return groupValidator.validateForGet(groupId).then {
            return groupRepository.get(groupId).then { Group newGroup ->
                if (newGroup == null) {
                    throw AppErrors.INSTANCE.groupNotFound(groupId).exception()
                }

                def callback = authorizeCallbackFactory.create(newGroup)
                return RightsScope.with(authorizeService.authorize(callback)) {
                    if (!AuthorizeContext.hasRights('read')) {
                        throw AppErrors.INSTANCE.groupNotFound(groupId).exception()
                    }

                    newGroup = groupFilter.filterForGet(newGroup, getOptions.properties?.split(',') as List<String>)
                    return Promise.pure(newGroup)
                }
            }
        }
    }

    @Override
    Promise<Results<Group>> list(GroupListOptions listOptions) {
        return groupValidator.validateForSearch(listOptions).then {
            def resultList = new Results<Group>(items: [])
            if (listOptions.organizationId != null) {
                return search(listOptions).then { List<Group> groupList ->
                    if (CollectionUtils.isEmpty(groupList)) {
                        return Promise.pure(resultList)
                    }

                    return Promise.each(groupList) { Group existingGroup ->
                        def callback = authorizeCallbackFactory.create(existingGroup)
                        return RightsScope.with(authorizeService.authorize(callback)) {
                            if (AuthorizeContext.hasRights('read')) {
                                Group filterGroup = groupFilter.filterForGet(existingGroup, listOptions.properties?.split(',') as List<String>)
                                resultList.items.add(filterGroup)
                                return Promise.pure(filterGroup)
                            } else {
                                return Promise.pure(null)
                            }
                        }
                    }.then {
                        return Promise.pure(resultList)
                    }
                }
            } else {
                return userGroupRepository.searchByUserId(listOptions.userId, listOptions.limit, listOptions.offset).then { List<UserGroup> userGroupList ->
                    if (CollectionUtils.isEmpty(userGroupList)) {
                        return Promise.pure(resultList)
                    }

                    return Promise.each(userGroupList) { UserGroup userGroup ->
                        return groupValidator.validateForGet(userGroup.groupId).then { Group existing ->
                            def callback = authorizeCallbackFactory.create(existing)
                            return RightsScope.with(authorizeService.authorize(callback)) {
                                if (AuthorizeContext.hasRights('read')) {
                                    existing = groupFilter.filterForGet(existing, listOptions.properties?.split(',') as List<String>)
                                    resultList.items.add(existing)
                                    return Promise.pure(existing)
                                } else {
                                    return Promise.pure(null)
                                }
                            }
                        }
                    }.then {
                        return Promise.pure(resultList)
                    }
                }
            }
        }
    }

    private Promise<List<Group>> search(GroupListOptions listOptions) {
        List<Group> result = new ArrayList<>()
        if (listOptions.organizationId != null && listOptions.name != null) {
            return groupRepository.searchByOrganizationIdAndName(listOptions.organizationId, listOptions.name,
                    listOptions.limit, listOptions.offset).then { Group group ->
                if (group != null) {
                    result.add(group)
                }

                return Promise.pure(result)
            }
        } else if (listOptions.organizationId != null) {
            return groupRepository.searchByOrganizationId(listOptions.organizationId, listOptions.limit, listOptions.offset)
        }
    }

    @Override
    Promise<Void> delete(GroupId groupId) {
        return groupValidator.validateForGet(groupId).then { Group existing ->
            def callback = authorizeCallbackFactory.create(existing)
            return RightsScope.with(authorizeService.authorize(callback)) {
                if (!AuthorizeContext.hasRights('delete')) {
                    throw AppCommonErrors.INSTANCE.forbidden().exception()
                }

                return groupRepository.delete(groupId)
            }
        }
    }
}
