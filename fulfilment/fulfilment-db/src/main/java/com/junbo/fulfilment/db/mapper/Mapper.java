/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.db.mapper;

import com.alibaba.fastjson.JSON;
import com.junbo.fulfilment.common.util.Utils;
import com.junbo.fulfilment.db.entity.*;
import com.junbo.fulfilment.spec.model.FulfilmentAction;
import com.junbo.fulfilment.spec.model.FulfilmentItem;
import com.junbo.fulfilment.spec.model.FulfilmentRequest;

import java.util.UUID;

/**
 * Mapper.
 */
public class Mapper {
    private static final String[] FULFILMENT_REQUEST_FILTER =
            new String[]{"requestId", "ordeId", "trackingGuid", "items"};

    private static final String[] FULFILMENT_ITEM_FILTER
            = new String[]{"fulfilmentId", "status", "actions", "requestId"};

    private static final String[] FULFILMENT_ACTION_FILTER =
            new String[]{"actionId", "status", "type", "result", "fulfilmentId"};


    private Mapper() {

    }

    public static FulfilmentRequest map(FulfilmentRequestEntity entity) {
        if (entity == null) {
            return null;
        }

        FulfilmentRequest model = JSON.parseObject(entity.getPayload(), FulfilmentRequest.class);
        model.setRequestId(entity.getId());
        model.setOrderId(entity.getOrderId());
        model.setTrackingGuid(entity.getTrackingGuid().toString());

        return model;
    }

    public static FulfilmentRequestEntity map(FulfilmentRequest model) {
        if (model == null) {
            return null;
        }

        FulfilmentRequestEntity entity = new FulfilmentRequestEntity();
        entity.setOrderId(model.getOrderId());
        entity.setTrackingGuid(UUID.fromString(model.getTrackingGuid()));
        entity.setPayload(Utils.toJson(model, FULFILMENT_REQUEST_FILTER));

        return entity;
    }

    public static FulfilmentItem map(FulfilmentEntity entity) {
        if (entity == null) {
            return null;
        }

        FulfilmentItem model = JSON.parseObject(entity.getPayload(), FulfilmentItem.class);
        model.setFulfilmentId(entity.getId());
        model.setRequestId(entity.getRequestId());

        return model;
    }

    public static FulfilmentEntity map(FulfilmentItem model) {
        if (model == null) {
            return null;
        }

        FulfilmentEntity entity = new FulfilmentEntity();
        entity.setRequestId(model.getRequestId());
        entity.setPayload(Utils.toJson(model, FULFILMENT_ITEM_FILTER));

        return entity;
    }

    public static FulfilmentAction map(FulfilmentActionEntity entity) {
        if (entity == null) {
            return null;
        }

        FulfilmentAction model = JSON.parseObject(entity.getPayload(), FulfilmentAction.class);
        model.setActionId(entity.getId());
        model.setStatus(entity.getStatus().toString());
        model.setType(entity.getType().toString());
        model.setResult(entity.getResult());
        model.setFulfilmentId(entity.getFulfilmentId());

        return model;
    }

    public static FulfilmentActionEntity map(FulfilmentAction model) {
        if (model == null) {
            return null;
        }

        FulfilmentActionEntity entity = new FulfilmentActionEntity();
        entity.setFulfilmentId(model.getFulfilmentId());
        entity.setType(FulfilmentActionType.valueOf(model.getType()));
        entity.setStatus(FulfilmentStatus.valueOf(model.getStatus()));
        entity.setResult(model.getResult());

        entity.setPayload(Utils.toJson(model, FULFILMENT_ACTION_FILTER));

        return entity;
    }
}
