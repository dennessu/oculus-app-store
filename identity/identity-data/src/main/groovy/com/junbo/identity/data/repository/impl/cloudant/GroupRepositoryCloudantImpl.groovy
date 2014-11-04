package com.junbo.identity.data.repository.impl.cloudant
import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.id.GroupId
import com.junbo.common.id.OrganizationId
import com.junbo.common.model.Results
import com.junbo.identity.data.repository.GroupRepository
import com.junbo.identity.spec.v1.model.Group
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.util.CollectionUtils

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
    Promise<Group> update(Group group, Group oldGroup) {
        return cloudantPut(group, oldGroup)
    }

    @Override
    Promise<Results<Group>> searchByOrganizationIdAndName(OrganizationId id, String name, Integer limit, Integer offset) {
        Results<Group> results = new Results<>()
        results.items = []
        def startKey = [id.toString(), name]
        def endKey = [id.toString(), name]
        return queryView('by_organization_id_and_name', startKey.toArray(new String()), endKey.toArray(new String()), false, limit, offset, false).then { List<Group> list ->
            if (!CollectionUtils.isEmpty(list)) {
                results.items.add(list.get(0))
            }

            return queryViewTotal('by_organization_id_and_name', startKey.toArray(new String()), endKey.toArray(new String()), false, false).then { Integer total ->
                results.total = total

                return Promise.pure(results)
            }
        }
    }

    @Override
    Promise<Results<Group>> searchByOrganizationId(OrganizationId id, Integer limit, Integer offset) {
        Results<Group> results = new Results<>()
        return queryView('by_organization_id', id.toString(), limit, offset, false).then { List<Group> groupList ->
            results.items = groupList

            return queryViewTotal('by_organization_id', id.toString()).then { Integer total ->
                results.total = total
                return Promise.pure(results)
            }
        }
    }

    @Override
    Promise<Void> delete(GroupId id) {
        return cloudantDelete(id.toString())
    }
}
