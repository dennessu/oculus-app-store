package com.junbo.identity.data.repository.impl.cloudant
import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.id.GroupId
import com.junbo.common.id.UserGroupId
import com.junbo.common.id.UserId
import com.junbo.identity.data.repository.UserGroupRepository
import com.junbo.identity.spec.v1.model.UserGroup
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
/**
 * Created by haomin on 14-4-11.
 */
@CompileStatic
class UserGroupRepositoryCloudantImpl extends CloudantClient<UserGroup> implements UserGroupRepository {

    @Override
    Promise<UserGroup> create(UserGroup entity) {
        return cloudantPost(entity)
    }

    @Override
    Promise<UserGroup> update(UserGroup entity) {
        return cloudantPut(entity)
    }

    @Override
    Promise<UserGroup> get(UserGroupId id) {
        return cloudantGet(id.toString())
    }

    @Override
    Promise<List<UserGroup>> searchByUserId(UserId userId, Integer limit, Integer offset) {
        return queryView('by_user_id', userId.toString(), limit, offset, false)
    }

    @Override
    Promise<List<UserGroup>> searchByGroupId(GroupId groupId, Integer limit, Integer offset) {
        return queryView('by_group_id', groupId.toString(), limit, offset, false)
    }

    @Override
    Promise<List<UserGroup>> searchByUserIdAndGroupId(UserId userId, GroupId groupId, Integer limit, Integer offset) {
        return queryView('by_user_id_group_id', "${userId.value}:${groupId.value}", limit, offset, false)
    }

    @Override
    Promise<Void> delete(UserGroupId id) {
        return cloudantDelete(id.toString())
    }
}
