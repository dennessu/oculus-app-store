/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.db.repo.cloudant

import com.junbo.common.id.OrderItemId
import com.junbo.langur.core.promise.Promise
import com.junbo.order.db.repo.OrderItemRepository
import com.junbo.order.spec.model.OrderItem
import com.junbo.sharding.repo.BaseCloudantRepositoryForDualWrite
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
/**
 * Created by chriszhu on 2/18/14.
 */
@CompileStatic
@TypeChecked
class OrderItemRepositoryCloudantImpl extends BaseCloudantRepositoryForDualWrite<OrderItem, OrderItemId> implements OrderItemRepository {

    @Override
    Promise<List<OrderItem>> getByOrderId(Long orderId) {
        return super.queryView('by_order', orderId.toString());
    }
}
