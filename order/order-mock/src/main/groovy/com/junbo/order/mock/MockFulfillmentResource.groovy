package com.junbo.order.mock
import com.junbo.common.id.FulfilmentId
import com.junbo.common.id.OrderId
import com.junbo.fulfilment.spec.model.FulfilmentItem
import com.junbo.fulfilment.spec.model.FulfilmentRequest
import com.junbo.fulfilment.spec.resource.FulfilmentResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.stereotype.Component
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
    Promise<FulfilmentRequest> getByOrderId(OrderId orderId) {
        return null
    }

    @Override
    Promise<FulfilmentItem> getByFulfilmentId(FulfilmentId billingOrderId) {
        return null
    }
}
