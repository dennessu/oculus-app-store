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
import com.junbo.identity.service.GroupService
import com.junbo.identity.service.UserGroupService
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

import javax.ws.rs.core.Response

/**
 * Created by xiali_000 on 4/8/2014.
 */
@Transactional
@CompileStatic
class GroupResourceImpl implements GroupResource {

    @Autowired
    private GroupService groupService

    @Autowired
    private UserGroupService userGroupService

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
        if (group == null) {
            throw AppCommonErrors.INSTANCE.requestBodyRequired().exception()
        }
        group = groupFilter.filterForCreate(group)

        return groupValidator.validateForCreate(group).then {
            def callback = authorizeCallbackFactory.create(group)
            return RightsScope.with(authorizeService.authorize(callback)) {
                if (!AuthorizeContext.hasRights('create')) {
                    throw AppCommonErrors.INSTANCE.forbidden().exception()
                }

                return groupService.create(group).then { Group newGroup ->
                    Created201Marker.mark(newGroup.getId())

                    newGroup = groupFilter.filterForGet(newGroup, null)
                    return Promise.pure(newGroup)
                }
            }
        }
    }

    @Override
    Promise<Group> put(GroupId groupId, Group group) {
        if (groupId == null) {
            throw AppCommonErrors.INSTANCE.parameterRequired('id').exception()
        }
        if (group == null) {
            throw AppCommonErrors.INSTANCE.requestBodyRequired().exception()
        }
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
                    return groupService.update(group, oldGroup).then { Group newGroup ->
                        newGroup = groupFilter.filterForGet(newGroup, null)
                        return Promise.pure(newGroup)
                    }
                }
            }
        }
    }

    @Override
    Promise<Group> patch(GroupId groupId, Group group) {
        if (groupId == null) {
            throw AppCommonErrors.INSTANCE.parameterRequired('id').exception()
        }
        if (group == null) {
            throw AppCommonErrors.INSTANCE.requestBodyRequired().exception()
        }
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
                    return groupService.update(group, oldGroup).then { Group newGroup ->
                        newGroup = groupFilter.filterForGet(newGroup, null)
                        return Promise.pure(newGroup)
                    }
                }
            }
        }
    }

    @Override
    Promise<Group> get(GroupId groupId, GroupGetOptions getOptions) {
        if (groupId == null) {
            throw AppCommonErrors.INSTANCE.parameterRequired('id').exception()
        }
        if (getOptions == null) {
            throw new IllegalArgumentException('getOptions is null')
        }

        return groupValidator.validateForGet(groupId).then {
            return groupService.get(groupId).then { Group newGroup ->
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
                return search(listOptions).then { Results<Group> groupList ->
                    resultList.total = groupList.total
                    if (groupList == null || CollectionUtils.isEmpty(groupList.items)) {
                        return Promise.pure(resultList)
                    }

                    return Promise.each(groupList.items) { Group existingGroup ->
                        def callback = authorizeCallbackFactory.create(existingGroup)
                        return RightsScope.with(authorizeService.authorize(callback)) {
                            if (AuthorizeContext.hasRights('read')) {
                                Group filterGroup = groupFilter.filterForGet(existingGroup, listOptions.properties?.split(',') as List<String>)
                                resultList.items.add(filterGroup)
                                return Promise.pure(filterGroup)
                            } else {
                                resultList.total = resultList.total - 1
                                return Promise.pure(null)
                            }
                        }
                    }.then {
                        return Promise.pure(resultList)
                    }
                }
            } else {
                return userGroupService.searchByUserId(listOptions.userId, listOptions.limit, listOptions.offset).then { Results<UserGroup> userGroupList ->
                    resultList.total = userGroupList.total
                    if (userGroupList == null || CollectionUtils.isEmpty(userGroupList.items)) {
                        return Promise.pure(resultList)
                    }

                    return Promise.each(userGroupList.items) { UserGroup userGroup ->
                        return groupValidator.validateForGet(userGroup.groupId).then { Group existing ->
                            def callback = authorizeCallbackFactory.create(existing)
                            return RightsScope.with(authorizeService.authorize(callback)) {
                                if (AuthorizeContext.hasRights('read')) {
                                    existing = groupFilter.filterForGet(existing, listOptions.properties?.split(',') as List<String>)
                                    resultList.items.add(existing)
                                    return Promise.pure(existing)
                                } else {
                                    resultList.total = resultList.total -1
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

    private Promise<Results<Group>> search(GroupListOptions listOptions) {
        if (listOptions.organizationId != null && listOptions.name != null) {
            return groupService.searchByOrganizationIdAndName(listOptions.organizationId, listOptions.name,
                    listOptions.limit, listOptions.offset)
        } else if (listOptions.organizationId != null) {
            return groupService.searchByOrganizationId(listOptions.organizationId, listOptions.limit, listOptions.offset)
        }
    }

    @Override
    Promise<Void> delete(GroupId groupId) {
        if (groupId == null) {
            throw AppCommonErrors.INSTANCE.parameterRequired('id').exception()
        }
        return groupValidator.validateForGet(groupId).then { Group existing ->
            def callback = authorizeCallbackFactory.create(existing)
            return RightsScope.with(authorizeService.authorize(callback)) {
                if (!AuthorizeContext.hasRights('delete')) {
                    throw AppCommonErrors.INSTANCE.forbidden().exception()
                }

                return groupService.delete(groupId).then {
                    return Promise.pure(Response.status(204).build())
                }
            }
        }
    }
}
