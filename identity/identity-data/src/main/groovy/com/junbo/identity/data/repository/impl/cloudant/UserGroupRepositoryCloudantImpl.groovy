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
        Results<UserGroup> results = new Results<>();
        return queryView('by_user_id', userId.toString(), limit, offset, false).then { List<UserGroup> userGroupList ->
            results.items = userGroupList

            return queryViewTotal('by_user_id', userId.toString()).then { Integer total ->
                results.total = total

                return Promise.pure(results)
            }
        }
    }

    @Override
    Promise<Results<UserGroup>> searchByGroupId(GroupId groupId, Integer limit, Integer offset) {
        Results<UserGroup> results = new Results<>();
        return queryView('by_group_id', groupId.toString(), limit, offset, false).then { List<UserGroup> userGroupList ->
            results.items = userGroupList

            return queryViewTotal('by_group_id', groupId.toString()).then { Integer total ->
                results.total = total

                return Promise.pure(results)
            }
        }
    }

    @Override
    Promise<Results<UserGroup>> searchByUserIdAndGroupId(UserId userId, GroupId groupId, Integer limit, Integer offset) {
        Results<UserGroup> results = new Results<>()
        def startKey = [userId.toString(), groupId.toString()]
        def endKey = [userId.toString(), groupId.toString()]
        return queryView('by_user_id_group_id', startKey.toArray(new String()), endKey.toArray(new String()),
                false, limit, offset, false).then { List<UserGroup> userGroupList ->
            results.items = userGroupList

            return queryViewTotal('by_user_id_group_id', startKey.toArray(new String()), endKey.toArray(new String()), false, false).then { Integer total ->
                results.total = total

                return Promise.pure(results)
            }
        }
    }

    @Override
    Promise<Void> delete(UserGroupId id) {
        return cloudantDelete(id.toString())
    }
}
