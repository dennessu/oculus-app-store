/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.db.repo.cloudant
import com.junbo.common.cloudant.model.CloudantViews
import com.junbo.common.id.SubledgerItemId
import com.junbo.langur.core.promise.Promise
import com.junbo.order.db.repo.SubledgerItemRepository
import com.junbo.order.spec.model.PageParam
import com.junbo.order.spec.model.SubledgerItem
import com.junbo.sharding.IdGenerator
import com.junbo.sharding.repo.BaseCloudantRepository
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.springframework.beans.factory.annotation.Required
/**
 * Created by chriszhu on 2/18/14.
 */
@CompileStatic
@TypeChecked
class SubledgerItemRepositoryCloudantImpl extends BaseCloudantRepository<SubledgerItem, SubledgerItemId> implements SubledgerItemRepository {

    private IdGenerator idGenerator

    @Required
    void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator
    }

    @Override
    protected SubledgerItemId generateId() {
        return new SubledgerItemId(idGenerator.nextId());
    }

    @Override
    protected CloudantViews getCloudantViews() {
        return views
    }

    @Override
    Promise<List<SubledgerItem>> getByStatus(Object shardKey, String status, PageParam pageParam) {
        throw new RuntimeException("SubledgerItemRepository::getByStatus is only available in SQL mode for backend jobs.");
    }

    private CloudantViews views = new CloudantViews(
        views: [:]
    )
}
