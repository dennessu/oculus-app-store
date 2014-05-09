/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common.apihelper.order.impl;

import com.junbo.common.json.JsonMessageTranscoder;
import com.junbo.langur.core.client.TypeReference;
import com.junbo.order.spec.model.OrderEvent;
import com.junbo.test.common.apihelper.order.OrderEventService;
import com.junbo.test.common.apihelper.HttpClientBase;
import com.junbo.test.common.libs.LogHelper;
import com.junbo.test.common.libs.RestUrl;

/**
 @author Jason
  * Time: 5/7/2014
  * The implementation for order event related APIs
 */
public class OrderEventServiceImpl extends HttpClientBase implements OrderEventService {

    private static String orderEventUrl = RestUrl.getRestUrl(RestUrl.ComponentName.COMMERCE) + "order-events";

    private LogHelper logger = new LogHelper(OrderEventServiceImpl.class);

    private static OrderEventService instance;

    public static synchronized OrderEventService getInstance() {
        if (instance == null) {
            instance = new OrderEventServiceImpl();
        }
        return instance;
    }

    private OrderEventServiceImpl() {
    }

    public OrderEvent postOrderEvent(OrderEvent orderEvent) throws Exception {
        return postOrderEvent(orderEvent, 200);
    }
    public OrderEvent postOrderEvent(OrderEvent orderEvent, int expectedResponseCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.POST, orderEventUrl, orderEvent, expectedResponseCode);
        return new JsonMessageTranscoder().decode(new TypeReference<OrderEvent>() {}, responseBody);
    }

}
