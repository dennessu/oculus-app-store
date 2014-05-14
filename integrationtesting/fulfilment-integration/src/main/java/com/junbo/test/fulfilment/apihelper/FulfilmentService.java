/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.fulfilment.apihelper;

import com.junbo.fulfilment.spec.model.FulfilmentRequest;

/**
 * Created by yunlongzhao 14/14.
 */
public interface FulfilmentService {

    String postFulfilment(FulfilmentRequest fulfilmentRequest) throws Exception;

    String postFulfilment(FulfilmentRequest fulfilmentRequest, int expectedResponseCode) throws Exception;

    String getFulfilmentByOrderId(String orderId) throws Exception;

    String getFulfilmentByOrderId(String orderId, int expectedResponseCode) throws Exception;

    String getFulfilment(String fulfilmentId) throws Exception;

    String getFulfilment(String fulfilmentId, int expectedResponseCode) throws Exception;

}
