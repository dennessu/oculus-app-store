/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.authorization.db.repository

import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.cloudant.model.CloudantViews
import com.junbo.common.id.RoleId
import com.junbo.authorization.spec.model.Role
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.IdGenerator
import com.junbo.sharding.ShardAlgorithm
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by Shenhua on 5/13/2014.
 */
@CompileStatic
class RoleRepositoryCloudantImpl extends CloudantClient<Role> implements RoleRepository {
    private ShardAlgorithm shardAlgorithm
    private IdGenerator idGenerator

    @Required
    void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator
    }

    @Required
    void setShardAlgorithm(ShardAlgorithm shardAlgorithm) {
        this.shardAlgorithm = shardAlgorithm
    }

    @Override
    protected CloudantViews getCloudantViews() {
        return views
    }

    @Override
    Promise<Role> create(Role role) {
        if (role.id == null) {
            role.id = new RoleId(idGenerator.nextId())
        }
        return Promise.pure((Role)super.cloudantPost(role))
    }

    @Override
    Promise<Role> get(RoleId roleId) {
        return Promise.pure((Role)super.cloudantGet(roleId.toString()))
    }

    @Override
    Promise<Role> update(Role role) {
        return Promise.pure((Role)super.cloudantPut(role))
    }

    @Override
    Promise<Void> delete(RoleId id) {
        throw new IllegalStateException('delete role not support')
    }

    @Override
    Promise<Role> findByRoleName(String roleName, String targetType, String filterType, String filterLink) {
        String key = "$roleName:$targetType:$filterType:$filterLink"
        def list = super.queryView('by_role_name', key)
        return list.size() > 0 ? Promise.pure(list[0]) : Promise.pure(null)
    }

    protected CloudantViews views = new CloudantViews(
            views: [
                    'by_role_name': new CloudantViews.CloudantView(
                            map: 'function(doc) {' +
                                    '  emit(doc.name + \':\' + doc.target.targetType + \':\'' +
                                    ' + doc.target.filterType + \':\' + doc.target.filterLink.href, doc._id)' +
                                        '}',
                            resultClass: String)
            ]
    )
}
