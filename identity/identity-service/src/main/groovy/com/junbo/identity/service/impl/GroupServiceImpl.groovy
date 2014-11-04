package com.junbo.identity.service.impl

import com.junbo.common.id.GroupId
import com.junbo.common.id.OrganizationId
import com.junbo.common.model.Results
import com.junbo.identity.data.repository.GroupRepository
import com.junbo.identity.service.GroupService
import com.junbo.identity.spec.v1.model.Group
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 10/21/14.
 */
@CompileStatic
class GroupServiceImpl implements GroupService {
    private GroupRepository groupRepository

    @Override
    Promise<Group> get(GroupId id) {
        return groupRepository.get(id)
    }

    @Override
    Promise<Group> getActiveGroup(GroupId id) {
        return get(id).then { Group group ->
            if (group.active) {
                return Promise.pure(group)
            }

            return Promise.pure(null)
        }
    }

    @Override
    Promise<Group> create(Group model) {
        return groupRepository.create(model)
    }

    @Override
    Promise<Group> update(Group model, Group oldModel) {
        return groupRepository.update(model, oldModel)
    }

    @Override
    Promise<Void> delete(GroupId id) {
        return groupRepository.delete(id)
    }

    @Override
    Promise<Results<Group>> searchByOrganizationIdAndName(OrganizationId id, String name, Integer limit, Integer offset) {
        return groupRepository.searchByOrganizationIdAndName(id, name, limit, offset)
    }

    @Override
    Promise<Results<Group>> searchByOrganizationId(OrganizationId id, Integer limit, Integer offset) {
        return groupRepository.searchByOrganizationId(id, limit, offset)
    }

    @Required
    void setGroupRepository(GroupRepository groupRepository) {
        this.groupRepository = groupRepository
    }
}
