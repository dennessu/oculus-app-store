/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.core.impl.orderaction.adapter

import com.junbo.order.core.impl.orderaction.context.BaseContext
import com.junbo.order.core.impl.orderaction.context.CreateOrderActionContext
import groovy.transform.CompileStatic

/**
 * Created by fzhang on 14-3-5.
 */
@CompileStatic
class CreateOrderActionAdapter extends ActionAdapter {

    @Override
    protected BaseContext processContext(BaseContext context) {
        def resultContext = new CreateOrderActionContext()
        return resultContext
    }
}
