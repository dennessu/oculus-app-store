/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.authorization.db.repository
import com.junbo.authorization.spec.model.Role
import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.cloudant.model.CloudantViews
import com.junbo.common.id.RoleId
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
/**
 * Created by Shenhua on 5/13/2014.
 */
@CompileStatic
class RoleRepositoryCloudantImpl extends CloudantClient<Role> implements RoleRepository {

    @Override
    protected CloudantViews getCloudantViews() {
        return views
    }

    @Override
    Promise<Role> create(Role role) {
        return cloudantPost(role)
    }

    @Override
    Promise<Role> get(RoleId roleId) {
        return super.cloudantGet(roleId.toString())
    }

    @Override
    Promise<Role> update(Role role) {
        return super.cloudantPut(role)
    }

    @Override
    Promise<Void> delete(RoleId id) {
        throw new IllegalStateException('delete role not support')
    }

    @Override
    Promise<Role> findByRoleName(String roleName, String targetType, String filterType,
                                 String filterLinkIdType, String filterLinkId) {
        String key = "$roleName:$targetType:$filterType:$filterLinkIdType:$filterLinkId"
        return queryView('by_role_name', key).then { List<Role> list ->
            return list.size() > 0 ? Promise.pure(list[0]) : Promise.pure(null)
        }
    }

    protected CloudantViews views = new CloudantViews(
            views: [
                    'by_role_name': new CloudantViews.CloudantView(
                            map: 'function(doc) {' +
                                    '  emit(doc.name + \':\' + doc.target.targetType + \':\'' +
                                    ' + doc.target.filterType + \':\' + doc.target.filterLinkIdType + \':\'' +
                                    ' + doc.target.filterLinkId, doc._id)' +
                                    '}',
                            resultClass: String)
            ]
    )
}
