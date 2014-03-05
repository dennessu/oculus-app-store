package com.junbo.order.core.impl.orderflow

import groovy.transform.CompileStatic

/**
 * Created by chriszhu on 2/7/14.
 */
@CompileStatic
class FreeSettleFlow extends BaseSettleFlow {
    @Override
    UUID getName() {
        return UUID.fromString('5FFCAD8E-AFB5-42E4-8432-D80871555EF3')
    }
}
