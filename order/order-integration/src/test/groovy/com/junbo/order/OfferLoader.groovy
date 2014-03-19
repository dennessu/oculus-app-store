package com.junbo.order

import com.junbo.order.test.ServiceFacade
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * Created by fzhang on 14-3-19.
 */
@Component('offerLoader')
class OfferLoader {

    @Autowired
    def ServiceFacade serviceFacade
}
