/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.core.impl.orderaction.adapter

import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.order.core.OrderAction
import com.junbo.order.core.impl.common.Constants
import com.junbo.order.core.impl.orderaction.context.BaseContext
import groovy.transform.CompileStatic
import org.springframework.beans.factory.InitializingBean

/**
 * Created by fzhang on 14-3-5.
 */
@CompileStatic
class ActionAdapter<T extends BaseContext> implements Action, InitializingBean {

    private OrderAction<T> orderAction

    private Class<OrderAction<T>> orderActionClass

    protected BaseContext processContext(BaseContext context) {
        return context
    }

    void setOrderAction(OrderAction<T> orderAction) {
        this.orderAction = orderAction
    }

    void setOrderActionClass(Class<OrderAction<T>> orderActionClass) {
        this.orderActionClass = orderActionClass
    }

    @Override
    final Promise<ActionResult> execute(ActionContext context) {
        def resultContext = (BaseContext) context.requestScope[Constants.ORDER_CONTEXT_SCOPE]
        resultContext = (T) processContext(resultContext)
        orderAction.execute(resultContext).syncThen {
            new ActionResult('')
        }
    }

    @Override
    void afterPropertiesSet() throws Exception {
        if (orderAction == null) {
            assert orderActionClass != null : 'orderActionClass should be set if orderAction is null'
            orderAction = orderActionClass.newInstance()
        }
    }
}
