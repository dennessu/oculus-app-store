package com.junbo.order.mock

import com.junbo.common.id.FulfilmentId
import com.junbo.common.id.OrderId
import com.junbo.fulfilment.spec.constant.FulfilmentStatus
import com.junbo.fulfilment.spec.model.FulfilmentAction
import com.junbo.fulfilment.spec.model.FulfilmentItem
import com.junbo.fulfilment.spec.model.FulfilmentRequest
import com.junbo.fulfilment.spec.resource.FulfilmentResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.springframework.stereotype.Component

import javax.validation.Valid
import java.util.concurrent.ConcurrentHashMap
/**
 * Created by fzhang on 14-3-7.
 */
@CompileStatic
@TypeChecked
@Component('mockFulfillmentResource')
class MockFulfillmentResource extends BaseMock implements FulfilmentResource {

    private final Map<Long, FulfilmentRequest> fulfilmentRequestMap = new ConcurrentHashMap<>()


    @Override
    Promise<FulfilmentRequest> fulfill(FulfilmentRequest request) {
        def id = generateLong()
        request.items?.each { FulfilmentItem item ->
            item.fulfilmentId = id
        }
        fulfilmentRequestMap[request.orderId] = request
        return Promise.pure(request)
    }

    @Override
    Promise<FulfilmentRequest> revoke(FulfilmentRequest request) {
        return null
    }

    @Override
    Promise<FulfilmentRequest> getByOrderId(OrderId orderId) {
        def request = fulfilmentRequestMap[orderId.value]
        if(request == null) {
            request = new FulfilmentRequest()
        }
        request.items?.each { FulfilmentItem fi ->
            fi.actions?.each { FulfilmentAction fa ->
                fa.status == FulfilmentStatus.SUCCEED
            }
        }
        return Promise.pure(request)
    }

    @Override
    Promise<FulfilmentItem> getByFulfilmentId(FulfilmentId billingOrderId) {
        return Promise.pure(null)
    }
}
