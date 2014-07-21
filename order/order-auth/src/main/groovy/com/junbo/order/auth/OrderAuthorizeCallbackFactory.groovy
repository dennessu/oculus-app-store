/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.auth

import com.junbo.authorization.AbstractAuthorizeCallbackFactory
import com.junbo.authorization.AuthorizeCallback
import com.junbo.common.id.OrderId
import com.junbo.common.id.UserId
import com.junbo.langur.core.promise.Promise
import com.junbo.order.spec.model.Order
import com.junbo.order.spec.resource.OrderResource
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * OrderAuthorizeCallbackFactory.
 */
@CompileStatic
class OrderAuthorizeCallbackFactory extends AbstractAuthorizeCallbackFactory<Order> {
    private OrderResource orderResource

    @Required
    void setOrderResource( OrderResource orderResource) {
        this.orderResource = orderResource
    }

    @Override
    AuthorizeCallback<Order> create(Order entity) {
        return new OrderAuthorizeCallback(this, entity)
    }

    AuthorizeCallback<Order> create(UserId userId) {
        return create(new Order(user: userId))
    }

    AuthorizeCallback<Order> create(OrderId orderId) {
        return create(orderResource.getOrderByOrderId(orderId).get())
    }
}
