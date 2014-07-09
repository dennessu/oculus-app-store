/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.core.service;

import com.junbo.common.error.AppCommonErrors;
import com.junbo.fulfilment.spec.model.FulfilmentAction;

import java.util.*;

/**
 * ClassifyResult.
 */
public class ClassifyResult {
    private Map<String, List<FulfilmentAction>> result;

    public ClassifyResult() {
        result = new HashMap();
    }

    public void addFulfilmentAction(FulfilmentAction action) {
        String type = action.getType();

        if (action.getType() == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired("fulfillmentAction.type").exception();
        }

        List<FulfilmentAction> actions = result.get(type);
        if (actions == null) {
            actions = new ArrayList();
            result.put(type, actions);
        }

        actions.add(action);
    }

    public List<FulfilmentAction> get(String actionType) {
        List<FulfilmentAction> actions = result.get(actionType);
        if (actions == null) {
            return Collections.EMPTY_LIST;
        }

        return actions;
    }

    public Set<String> getActionTypes() {
        return result.keySet();
    }
}
