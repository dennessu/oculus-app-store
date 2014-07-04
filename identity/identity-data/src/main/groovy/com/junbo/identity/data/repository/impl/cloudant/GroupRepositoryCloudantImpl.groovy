package com.junbo.identity.data.repository.impl.cloudant
import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.id.GroupId
import com.junbo.common.id.OrganizationId
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
        return cloudantGet(groupId.toString())
    }

    @Override
    Promise<Group> create(Group group) {
        return cloudantPost(group)
    }

    @Override
    Promise<Group> update(Group group) {
        return cloudantPut(group)
    }

    @Override
    Promise<Group> searchByOrganizationIdAndName(OrganizationId id, String name, Integer limit, Integer offset) {
        def startKey = [id.toString(), name]
        def endKey = [id.toString(), name]
        return queryView('by_organization_id_and_name', startKey.toArray(new String()), endKey.toArray(new String()), false, limit, offset, false).then { List<Group> list ->
            return list.size() > 0 ? Promise.pure(list[0]) : Promise.pure(null)
        }
    }

    @Override
    Promise<List<Group>> searchByOrganizationId(OrganizationId id, Integer limit, Integer offset) {
        return queryView('by_organization_id', id.toString(), limit, offset, false).then { List<Group> groupList ->
            return Promise.pure(groupList)
        }
    }

    @Override
    Promise<Void> delete(GroupId id) {
        throw new IllegalStateException('delete group not support')
    }
}
