/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.auth

import com.junbo.authorization.AbstractAuthorizeCallback
import com.junbo.common.id.UserId
import com.junbo.order.spec.model.Order
import groovy.transform.CompileStatic

/**
 * OrderAuthorizeCallback.
 */
@CompileStatic
class OrderAuthorizeCallback extends AbstractAuthorizeCallback<Order> {
    @Override
    String getApiName() {
        return 'orders'
    }

    OrderAuthorizeCallback(OrderAuthorizeCallbackFactory factory, Order entity) {
        super(factory, entity)
    }

    @Override
    protected UserId getUserOwnerId() {
        Order order = entity as Order
        if (order != null) {
            return order.user as UserId
        }

        return null
    }
}
