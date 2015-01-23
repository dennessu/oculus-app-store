/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.clientproxy.fulfillment;

import com.junbo.common.id.OrderId;
import com.junbo.fulfilment.spec.model.FulfilmentRequest;
import com.junbo.langur.core.promise.Promise;
import com.junbo.order.spec.model.Order;

/**
 * Interface for fulfillment facade.
 */
public interface FulfillmentFacade {

    Promise<FulfilmentRequest> postFulfillment(Order order);

    Promise<FulfilmentRequest> getFulfillment(OrderId orderId);

    Promise<FulfilmentRequest> reverseFulfillment(Order order);
}
