/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.db.repo.cloudant
import com.junbo.common.cloudant.model.CloudantViews
import com.junbo.common.id.OrderEventId
import com.junbo.langur.core.promise.Promise
import com.junbo.order.db.repo.OrderEventRepository
import com.junbo.order.spec.model.OrderEvent
import com.junbo.order.spec.model.PageParam
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
class OrderEventRepositoryCloudantImpl extends BaseCloudantRepository<OrderEvent, OrderEventId> implements OrderEventRepository {

    private IdGenerator idGenerator

    @Required
    void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator
    }

    @Override
    protected OrderEventId generateId() {
        return new OrderEventId(idGenerator.nextId());
    }

    @Override
    protected CloudantViews getCloudantViews() {
        return views
    }

    @Override
    Promise<List<OrderEvent>> getByOrderId(Long orderId, PageParam pageParam) {
        List<OrderEvent> list = super.queryView('by_order', orderId.toString(), pageParam?.count, pageParam?.start, false);
        return Promise.pure(list);
    }

    private CloudantViews views = new CloudantViews(
        views: [
            'by_order': new CloudantViews.CloudantView(
                    map: 'function(doc) {' +
                            'if (doc.order) {' +
                                'emit(doc.order, doc._id)' +
                            '}' +
                         '}',
                    resultClass: String)
        ]
    )
}
