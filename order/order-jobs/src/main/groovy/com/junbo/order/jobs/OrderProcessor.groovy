package com.junbo.order.jobs

import com.junbo.order.spec.model.Order
import groovy.transform.CompileStatic

/**
 * Created by fzhang on 4/18/2014.
 */
@CompileStatic
interface OrderProcessor {
    OrderProcessResult process(Order order)
}