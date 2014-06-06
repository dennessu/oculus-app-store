/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.repo.cloudant
import com.junbo.common.cloudant.model.CloudantViews
import com.junbo.langur.core.promise.Promise
import com.junbo.order.db.repo.BillingHistoryRepository
import com.junbo.order.spec.model.BillingHistory
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
class BillingHistoryRepositoryCloudantImpl extends BaseCloudantRepository<BillingHistory, Long> implements BillingHistoryRepository {

    private IdGenerator idGenerator

    @Required
    void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator
    }

    @Override
    protected Long generateId() {
        return idGenerator.nextId();
    }

    @Override
    protected CloudantViews getCloudantViews() {
        return views
    }

    @Override
    Promise<List<BillingHistory>> getByOrderId(Long orderId) {
        List<BillingHistory> list = super.queryView('by_order', orderId.toString());
        return Promise.pure(list);
    }

    private CloudantViews views = new CloudantViews(
        views: [
            'by_order': new CloudantViews.CloudantView(
                    map: 'function(doc) {' +
                            'if (doc.orderId) {' +
                                'emit(doc.orderId, doc._id)' +
                            '}' +
                         '}',
                    resultClass: String)
        ]
    )
}
