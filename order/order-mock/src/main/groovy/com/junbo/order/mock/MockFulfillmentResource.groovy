package com.junbo.order.mock
import com.junbo.common.id.FulfilmentId
import com.junbo.common.id.OrderId
import com.junbo.fulfilment.spec.constant.FulfilmentActionType
import com.junbo.fulfilment.spec.constant.FulfilmentStatus
import com.junbo.fulfilment.spec.model.FulfilmentAction
import com.junbo.fulfilment.spec.model.FulfilmentItem
import com.junbo.fulfilment.spec.model.FulfilmentRequest
import com.junbo.fulfilment.spec.model.FulfilmentResult
import com.junbo.fulfilment.spec.resource.FulfilmentResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.apache.commons.collections.CollectionUtils
import org.springframework.stereotype.Component

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
            item.actions = [new FulfilmentAction(
                    type: FulfilmentActionType.GRANT_ENTITLEMENT,
                    status: FulfilmentStatus.SUCCEED,
                    result: new FulfilmentResult(
                            entitlementIds: [generateString()]
                    )
            )]
        }
        fulfilmentRequestMap[request.orderId] = request
        return Promise.pure(request)
    }

    @Override
    Promise<FulfilmentRequest> revoke(FulfilmentRequest request) {
        def id = generateLong()
        request.items?.each { FulfilmentItem item ->
            item.fulfilmentId = id
            item.actions = [new FulfilmentAction(
                    type: FulfilmentActionType.GRANT_ENTITLEMENT,
                    status: FulfilmentStatus.REVOKED
            )]
        }
        fulfilmentRequestMap[request.orderId] = request
        return Promise.pure(request)
    }

    @Override
    Promise<FulfilmentRequest> getByOrderId(OrderId orderId) {
        def request = fulfilmentRequestMap[orderId.value]
        if(request == null) {
            request = new FulfilmentRequest()
        }
        request.items?.each { FulfilmentItem fi ->
            if (CollectionUtils.isEmpty(fi.actions)) {
                fi.actions = [new FulfilmentAction(
                        type: FulfilmentActionType.GRANT_ENTITLEMENT,
                        status: FulfilmentStatus.SUCCEED,
                        result: new FulfilmentResult(
                                entitlementIds: [generateString()]
                        )
                )]
            }
        }
        return Promise.pure(request)
    }

    @Override
    Promise<FulfilmentItem> getByFulfilmentId(FulfilmentId billingOrderId) {
        return Promise.pure(null)
    }
}
