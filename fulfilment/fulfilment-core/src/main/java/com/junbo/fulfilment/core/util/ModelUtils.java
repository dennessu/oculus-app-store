/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.core.util;

import com.junbo.fulfilment.spec.model.FulfilmentAction;
import com.junbo.fulfilment.spec.model.FulfilmentItem;
import com.junbo.fulfilment.spec.model.FulfilmentRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * ModelUtils.
 */
public class ModelUtils {
    private ModelUtils() {

    }

    public static Map<Long, FulfilmentItem> buildFulfilmentItemMap(FulfilmentRequest request) {
        if (request == null || request.getItems() == null) {
            return null;
        }

        Map<Long, FulfilmentItem> result = new HashMap<>();
        for (FulfilmentItem item : request.getItems()) {
            result.put(item.getFulfilmentId(), item);
        }

        return result;
    }

    public static Map<Long, FulfilmentAction> buildFulfilmentActionMap(FulfilmentItem item) {
        if (item == null || item.getActions() == null) {
            return null;
        }

        Map<Long, FulfilmentAction> result = new HashMap<>();
        for (FulfilmentAction action : item.getActions()) {
            result.put(action.getActionId(), action);
        }

        return result;
    }
}
