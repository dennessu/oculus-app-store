/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.core.impl.orderflow

import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.order.core.FlowExecutor
import com.junbo.order.core.FlowType
import com.junbo.order.core.impl.common.Constants
import com.junbo.order.core.impl.order.OrderServiceContext
import com.junbo.order.core.impl.orderaction.context.BaseContext
import com.junbo.order.spec.model.Order
import org.springframework.beans.factory.annotation.Autowired

/**
 * Created by fzhang on 14-3-5.
 */
class FlowExecutorImpl implements FlowExecutor {

    @Autowired
    com.junbo.langur.core.webflow.executor.FlowExecutor flowExecutor

    @Override
    Promise<List<Order>> executeFlow(FlowType flowType, OrderServiceContext order) {
        BaseContext context = new BaseContext()
        context.setOrderServiceContext(order)
        def requestScope = [(Constants.ORDER_CONTEXT_SCOPE):context]
        flowExecutor.start(flowType.name(), requestScope).syncThen { ActionContext actionContext ->
            def resultContext = (BaseContext) actionContext.requestScope[Constants.ORDER_CONTEXT_SCOPE]
            return [resultContext.orderServiceContext.order]
        }
    }
}
