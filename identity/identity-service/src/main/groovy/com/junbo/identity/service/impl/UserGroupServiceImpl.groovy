package com.junbo.identity.service.impl

import com.junbo.common.id.GroupId
import com.junbo.common.id.UserGroupId
import com.junbo.common.id.UserId
import com.junbo.identity.data.repository.UserGroupRepository
import com.junbo.identity.service.UserGroupService
import com.junbo.identity.spec.v1.model.UserGroup
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 10/21/14.
 */
@CompileStatic
class UserGroupServiceImpl implements UserGroupService {
    private UserGroupRepository userGroupRepository

    @Override
    Promise<UserGroup> get(UserGroupId id) {
        return userGroupRepository.get(id)
    }

    @Override
    Promise<UserGroup> create(UserGroup model) {
        return userGroupRepository.create(model)
    }

    @Override
    Promise<UserGroup> update(UserGroup model, UserGroup oldModel) {
        return userGroupRepository.update(model, oldModel)
    }

    @Override
    Promise<Void> delete(UserGroupId id) {
        return userGroupRepository.delete(id)
    }

    @Override
    Promise<List<UserGroup>> searchByUserId(UserId userId, Integer limit, Integer offset) {
        return userGroupRepository.searchByUserId(userId, limit, offset)
    }

    @Override
    Promise<List<UserGroup>> searchByGroupId(GroupId groupId, Integer limit, Integer offset) {
        return userGroupRepository.searchByGroupId(groupId, limit, offset)
    }

    @Override
    Promise<List<UserGroup>> searchByUserIdAndGroupId(UserId userId, GroupId groupId, Integer limit, Integer offset) {
        return userGroupRepository.searchByUserIdAndGroupId(userId, groupId, limit, offset)
    }

    @Required
    void setUserGroupRepository(UserGroupRepository userGroupRepository) {
        this.userGroupRepository = userGroupRepository
    }
}
