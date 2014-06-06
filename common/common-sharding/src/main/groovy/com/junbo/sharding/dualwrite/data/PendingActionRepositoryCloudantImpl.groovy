/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.sharding.dualwrite.data
import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.cloudant.model.CloudantViews
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
/**
 * The pending action repo implemented by Cloudant.
 * Since we cannot include the virtual CloudantEntity in the member,
 * we still convert it to PendingActionEntity before persistent.
 */
@CompileStatic
public class PendingActionRepositoryCloudantImpl extends CloudantClient<PendingActionEntity> implements PendingActionRepository {

    private boolean hardDelete;
    private PendingActionMapper mapper;

    @Required
    void setMapper(PendingActionMapper mapper) {
        this.mapper = mapper
    }

    void setHardDelete(boolean hardDelete) {
        this.hardDelete = hardDelete
    }

    boolean getHardDelete() {
        return hardDelete
    }

    @Override
    protected CloudantViews getCloudantViews() {
        return views
    }

    @Override
    public Promise<PendingAction> get(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("id is null");
        }
        PendingActionEntity entity = this.cloudantGet(id.toString());
        if (entity != null && entity.isDeleted()) {
            return Promise.pure(null);
        }

        return Promise.pure(mapper.map(entity));
    }

    @Override
    public Promise<PendingAction> create(PendingAction model) {
        if (model == null) {
            throw new IllegalArgumentException('model is null')
        }
        if (model.id == null) {
            model.id = UUID.randomUUID()
        }
        PendingActionEntity entity = mapper.map(model)
        this.cloudantPost(entity)

        return get(model.getId())
    }

    @Override
    public Promise<PendingAction> update(PendingAction model) {
        return Promise.pure(mapper.map(this.cloudantPut(mapper.map(model))))
    }

    @Override
    public Promise<Void> delete(UUID id) {
        this.cloudantDelete(id.toString())
        return Promise.pure(null)
    }

    @Override
    public Promise<List<PendingAction>> list(Integer dc, Integer shardId, int maxSize) {
        return null;
    }

    protected CloudantViews views = new CloudantViews(
        views: [:]
    )
}
