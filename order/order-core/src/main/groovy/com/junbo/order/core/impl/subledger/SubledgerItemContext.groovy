package com.junbo.order.core.impl.subledger

import com.junbo.common.id.OfferId
import com.junbo.common.id.UserId
import groovy.transform.CompileStatic

/**
 * Created by fzhang on 4/10/2014.
 */
@CompileStatic
class SubledgerItemContext {

    UserId sellerId

    OfferId offerId

    String currency

    String country

    Date createdTime
}
