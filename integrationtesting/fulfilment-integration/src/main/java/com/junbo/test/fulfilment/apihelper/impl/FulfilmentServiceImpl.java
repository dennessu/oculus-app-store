/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.fulfilment.apihelper.impl;

import com.junbo.common.id.FulfilmentId;
import com.junbo.fulfilment.spec.model.FulfilmentItem;
import com.junbo.fulfilment.spec.model.FulfilmentRequest;
import com.junbo.test.common.apihelper.HttpClientBase;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.test.common.libs.RestUrl;
import com.junbo.test.fulfilment.apihelper.FulfilmentService;
import com.junbo.common.json.JsonMessageTranscoder;
import com.junbo.langur.core.client.TypeReference;

/**
 * Created by weiyu_000 on 5/14/14.
 */
public class FulfilmentServiceImpl extends HttpClientBase implements FulfilmentService {

    private static String fulfilmentUrl = RestUrl.getRestUrl(RestUrl.ComponentName.COMMERCE);
    private static FulfilmentService instance;

    public static synchronized FulfilmentService getInstance() {
        if (instance == null) {
            instance = new FulfilmentServiceImpl();
        }
        return instance;
    }

    @Override
    public String postFulfilment(FulfilmentRequest fulfilment) throws Exception {
        return postFulfilment(fulfilment, 200);
    }

    @Override
    public String postFulfilment(FulfilmentRequest fulfilment, int expectedResponseCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.POST, fulfilmentUrl + "fulfilments", fulfilment,
                expectedResponseCode);

        FulfilmentItem fulfilmentItemResult =
                new JsonMessageTranscoder().decode(
                        new TypeReference<FulfilmentItem>() {
                        }, responseBody);

        String fulfilmentId = IdConverter.idLongToHexString(FulfilmentId.class, fulfilmentItemResult.getFulfilmentId());
        Master.getInstance().addFulfilment(fulfilmentId, fulfilmentItemResult);

        return fulfilmentId;
    }

    @Override
    public String getFulfilmentByOrderId(String orderId) throws Exception {
        return getFulfilmentByOrderId(orderId, 200);
    }

    @Override
    public String getFulfilmentByOrderId(String orderId, int expectedResponseCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.GET, String.format(fulfilmentUrl + "fulfilments?=orderId=%s",
                orderId), expectedResponseCode);

        FulfilmentItem fulfilmentItemResult =
                new JsonMessageTranscoder().decode(
                        new TypeReference<FulfilmentItem>() {
                        }, responseBody);

        String fulfilmentId = IdConverter.idLongToHexString(FulfilmentId.class, fulfilmentItemResult.getFulfilmentId());
        Master.getInstance().addFulfilment(fulfilmentId, fulfilmentItemResult);

        return fulfilmentId;
    }

    @Override
    public String getFulfilment(String fulfilmentId) throws Exception {
        return getFulfilment(fulfilmentId, 200);
    }

    @Override
    public String getFulfilment(String fulfilmentId, int expectedResponseCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.GET, fulfilmentUrl + "fulfilments/" + fulfilmentId,
                expectedResponseCode);

        FulfilmentItem fulfilmentItemResult =
                new JsonMessageTranscoder().decode(
                        new TypeReference<FulfilmentItem>() {
                        }, responseBody);

        fulfilmentId = IdConverter.idLongToHexString(FulfilmentId.class, fulfilmentItemResult.getFulfilmentId());
        Master.getInstance().addFulfilment(fulfilmentId, fulfilmentItemResult);

        return fulfilmentId;
    }

}
