package com.junbo.token.core.service;

import com.junbo.common.id.FulfilmentId;
import com.junbo.common.id.OrderId;
import com.junbo.fulfilment.spec.model.FulfilmentItem;
import com.junbo.fulfilment.spec.model.FulfilmentRequest;
import com.junbo.fulfilment.spec.resource.FulfilmentResource;
import com.junbo.langur.core.promise.Promise;

import javax.validation.Valid;

/**
 * Created by Administrator on 14-7-4.
 */
public class MockFulfilmentResource implements FulfilmentResource {
    @Override
    public Promise<FulfilmentRequest> fulfill(@Valid FulfilmentRequest request) {
        return Promise.pure(new FulfilmentRequest());
    }

    @Override
    public Promise<FulfilmentRequest> getByOrderId(OrderId orderId) {
        return null;
    }

    @Override
    public Promise<FulfilmentItem> getByFulfilmentId(FulfilmentId fulfilmentId) {
        return null;
    }
}
