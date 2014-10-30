package com.junbo.identity.service

import com.junbo.common.id.GroupId
import com.junbo.common.id.UserGroupId
import com.junbo.common.id.UserId
import com.junbo.identity.spec.v1.model.UserGroup
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 10/21/14.
 */
@CompileStatic
public interface UserGroupService {
    Promise<UserGroup> get(UserGroupId id)

    Promise<UserGroup> create(UserGroup model)

    Promise<UserGroup> update(UserGroup model, UserGroup oldModel)

    Promise<Void> delete(UserGroupId id)

    Promise<List<UserGroup>> searchByUserId(UserId userId, Integer limit, Integer offset)

    Promise<List<UserGroup>> searchByGroupId(GroupId groupId, Integer limit, Integer offset)

    Promise<List<UserGroup>> searchByUserIdAndGroupId(UserId userId, GroupId groupId, Integer limit, Integer offset)
}