package com.junbo.identity.data.repository.impl.cloudant
import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.id.GroupId
import com.junbo.identity.data.repository.GroupRepository
import com.junbo.identity.spec.v1.model.Group
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
/**
 * Created by haomin on 14-4-10.
 */
@CompileStatic
class GroupRepositoryCloudantImpl extends CloudantClient<Group> implements GroupRepository {

    @Override
    Promise<Group> get(GroupId groupId) {
        return super.cloudantGet(groupId.toString())
    }

    @Override
    Promise<Group> create(Group group) {
        return super.cloudantPost(group)
    }

    @Override
    Promise<Group> update(Group group) {
        return super.cloudantPut(group)
    }

    @Override
    Promise<Group> searchByName(String name) {
        return super.queryView('by_name', name).then { List<Group> list ->
            return list.size() > 0 ? Promise.pure(list[0]) : Promise.pure(null)
        }
    }

    @Override
    Promise<Void> delete(GroupId id) {
        throw new IllegalStateException('delete group not support')
    }
}
