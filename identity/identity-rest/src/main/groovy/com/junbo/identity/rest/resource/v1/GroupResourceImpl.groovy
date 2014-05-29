/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.resource.v1

import com.junbo.common.id.GroupId
import com.junbo.common.id.Id
import com.junbo.common.model.Results
import com.junbo.common.rs.Created201Marker
import com.junbo.identity.core.service.filter.GroupFilter
import com.junbo.identity.core.service.validator.GroupValidator
import com.junbo.identity.data.repository.GroupRepository
import com.junbo.identity.data.repository.UserGroupRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.Group
import com.junbo.identity.spec.v1.model.UserGroup
import com.junbo.identity.spec.v1.option.list.GroupListOptions
import com.junbo.identity.spec.v1.option.list.UserGroupListOptions
import com.junbo.identity.spec.v1.option.model.GroupGetOptions
import com.junbo.identity.spec.v1.resource.GroupResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
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

    @Override
    Promise<Group> create(Group group) {
        group = groupFilter.filterForCreate(group)

        return groupValidator.validateForCreate(group).then {
            return groupRepository.create(group).then { Group newGroup ->
                Created201Marker.mark((Id) newGroup.id)

                newGroup = groupFilter.filterForGet(newGroup, null)
                return Promise.pure(newGroup)
            }
        }
    }

    @Override
    Promise<Group> put(GroupId groupId, Group group) {

        return groupValidator.validateForGet(groupId).then { Group oldGroup ->
            if (oldGroup == null) {
                throw AppErrors.INSTANCE.groupNotFound(groupId).exception()
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

    @Override
    Promise<Group> patch(GroupId groupId, Group group) {

        return groupValidator.validateForGet(groupId).then { Group oldGroup ->
            if (oldGroup == null) {
                throw AppErrors.INSTANCE.groupNotFound(groupId).exception()
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

                newGroup = groupFilter.filterForGet(newGroup, getOptions.properties?.split(',') as List<String>)
                return Promise.pure(newGroup)
            }
        }
    }

    @Override
    Promise<Results<Group>> list(GroupListOptions listOptions) {
        return groupValidator.validateForSearch(listOptions).then {
            def resultList = new Results<Group>(items: [])
            if (listOptions.name != null) {
                return groupRepository.searchByName(listOptions.name).then { Group newGroup ->
                    if (newGroup != null) {
                        newGroup = groupFilter.filterForGet(newGroup, listOptions.properties?.split(',') as List<String>)
                    }

                    if (newGroup != null) {
                        resultList.items.add(newGroup)
                    }

                    return Promise.pure(resultList)
                }
            } else {
                return userGroupRepository.searchByUserId(listOptions.userId, listOptions.limit,
                        listOptions.offset).then { List<UserGroup> userGroupList ->
                    return fillUserGroups(userGroupList.iterator(), resultList, listOptions).then {
                        return Promise.pure(resultList)
                    }
                }
            }
        }
    }

    private Promise<Void> fillUserGroups(Iterator<UserGroup> it, Results<Group> resultList,
                                         GroupListOptions listOptions) {
        if (it.hasNext()) {
            UserGroup userGroup = it.next()
            return groupValidator.validateForGet(userGroup.groupId).then { Group existing ->
                existing = groupFilter.filterForGet(existing, listOptions.properties?.split(',') as List<String>)
                resultList.items.add(existing)

                return fillUserGroups(it, resultList, listOptions)
            }
        }

        return Promise.pure(null)
    }

    @Override
    Promise<Void> delete(GroupId groupId) {
        return groupValidator.validateForGet(groupId).then {
            return groupRepository.delete(groupId)
        }
    }
}
