/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.db.repo.cloudant
import com.junbo.common.cloudant.model.CloudantViews
import com.junbo.common.id.OrderItemId
import com.junbo.langur.core.promise.Promise
import com.junbo.order.db.repo.OrderItemRepository
import com.junbo.order.spec.model.OrderItem
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
class OrderItemRepositoryCloudantImpl extends BaseCloudantRepository<OrderItem, OrderItemId> implements OrderItemRepository {

    private IdGenerator idGenerator

    @Required
    void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator
    }

    @Override
    protected OrderItemId generateId() {
        return new OrderItemId(idGenerator.nextId());
    }

    @Override
    protected CloudantViews getCloudantViews() {
        return views
    }

    @Override
    Promise<List<OrderItem>> getByOrderId(Long orderId) {
        List<OrderItem> list = super.queryView('by_order', orderId.toString());
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
