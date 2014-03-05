package com.junbo.order.core.impl.orderflow

import groovy.transform.CompileStatic

/**
 * Created by chriszhu on 2/7/14.
 */
@CompileStatic
class AsyncSettleFlow extends BaseSettleFlow {
    @Override
    UUID getName() {
        return UUID.fromString('FE96615E-43C1-4E8D-822D-DA625265D82B')
    }
}
