package com.junbo.identity.data.repository.impl.cloudant
import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.id.GroupId
import com.junbo.common.id.UserGroupId
import com.junbo.common.id.UserId
import com.junbo.common.model.Results
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
    Promise<UserGroup> update(UserGroup entity, UserGroup oldEntity) {
        return cloudantPut(entity, oldEntity)
    }

    @Override
    Promise<UserGroup> get(UserGroupId id) {
        return cloudantGet(id.toString())
    }

    @Override
    Promise<Results<UserGroup>> searchByUserId(UserId userId, Integer limit, Integer offset) {
        return queryViewResults('by_user_id', userId.toString(), limit, offset, false)
    }

    @Override
    Promise<Results<UserGroup>> searchByGroupId(GroupId groupId, Integer limit, Integer offset) {
        return queryViewResults('by_group_id', groupId.toString(), limit, offset, false)
    }

    @Override
    Promise<Results<UserGroup>> searchByUserIdAndGroupId(UserId userId, GroupId groupId, Integer limit, Integer offset) {
        def startKey = [userId.toString(), groupId.toString()]
        def endKey = [userId.toString(), groupId.toString()]
        // todo:    The reason why we add one new view here is because the query view doesn't have reduce=false
        // To not break any code, we will add one new view
        // After the deploy, we will remove the reduce one
        return queryViewResults('by_user_id_group_id_with_reduce', startKey.toArray(new String()), endKey.toArray(new String()),
                false, limit, offset, false)
    }

    @Override
    Promise<Void> delete(UserGroupId id) {
        return cloudantDelete(id.toString())
    }
}
