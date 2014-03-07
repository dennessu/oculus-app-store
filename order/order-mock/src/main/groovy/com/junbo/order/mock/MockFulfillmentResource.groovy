package com.junbo.order.mock

import com.junbo.fulfilment.spec.model.FulfilmentItem
import com.junbo.fulfilment.spec.model.FulfilmentRequest
import com.junbo.fulfilment.spec.resource.FulfilmentResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.stereotype.Component

import javax.ws.rs.PathParam
import javax.ws.rs.QueryParam

/**
 * Created by fzhang on 14-3-7.
 */
@CompileStatic
@Component('mockFulfillmentResource')
class MockFulfillmentResource extends BaseMock implements FulfilmentResource {

    @Override
    Promise<FulfilmentRequest> fulfill(FulfilmentRequest request) {
        def id = generateLong()
        request.items?.each { FulfilmentItem item ->
            item.fulfilmentId = id
        }
        return Promise.pure(request)
    }

    @Override
    Promise<FulfilmentRequest> getByBillingOrderId(@QueryParam('billingOrderId') Long billingOrderId) {
        return null
    }

    @Override
    Promise<FulfilmentItem> getByFulfilmentId(@PathParam('fulfilmentId') Long billingOrderId) {
        return null
    }
}
