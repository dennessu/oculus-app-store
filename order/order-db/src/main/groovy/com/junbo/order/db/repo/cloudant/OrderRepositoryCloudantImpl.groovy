/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.db.repo.cloudant

import com.junbo.common.cloudant.model.CloudantViews
import com.junbo.common.id.OrderId
import com.junbo.langur.core.promise.Promise
import com.junbo.order.db.repo.OrderRepository
import com.junbo.order.spec.model.Order
import com.junbo.order.spec.model.OrderQueryParam
import com.junbo.order.spec.model.PageParam
import com.junbo.sharding.repo.BaseCloudantRepositoryForDualWrite
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked

/**
 * Created by chriszhu on 2/18/14.
 */
@CompileStatic
@TypeChecked
class OrderRepositoryCloudantImpl extends BaseCloudantRepositoryForDualWrite<Order, OrderId> implements OrderRepository {

    @Override
    protected CloudantViews getCloudantViews() {
        return views
    }

    @Override
    Promise<List<Order>> getByUserId(Long userId, OrderQueryParam orderQueryParam, PageParam pageParam) {
        if (orderQueryParam.getTentative() == null) {
            return super.queryView('by_user', userId.toString(), pageParam?.count, pageParam?.start, false);
        } else {
            return super.queryView('by_user_tentative', userId.toString() + "_" + orderQueryParam.tentative, pageParam?.count, pageParam?.start, false);
        }
    }

    @Override
    Promise<List<Order>> getByStatus(Object shardKey, List<String> statusList, boolean updatedByAscending, PageParam pageParam) {
        throw new RuntimeException("OrderRepository::getByStatus is only available in SQL mode for backend jobs.");
    }

    private CloudantViews views = new CloudantViews(
        views: [
            'by_user': new CloudantViews.CloudantView(
                    map: 'function(doc) {' +
                            'emit(doc.user, doc._id)' +
                         '}',
                    resultClass: String),
            'by_user_tentitive': new CloudantViews.CloudantView(
                    map: 'function(doc) {' +
                            'if (doc.tentative !== null) {' +
                                'emit(doc.user + "_" + doc.tentative, doc._id)' +
                            '}' +
                         '}',
                    resultClass: String)
        ]
    )
}
