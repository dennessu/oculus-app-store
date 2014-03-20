/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.docs.app.resultlists;

import com.junbo.identity.spec.model.common.ResultList;
import com.junbo.order.spec.model.Order;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * The non-generic ResultList types for identity.
 */
public class OrderResultLists {

    /**
     * Find the non-generic ResultList type.
     */
    public static Class getClass(ParameterizedType type) {
        Type actualType = type.getActualTypeArguments()[0];
        return resultListMap.get(actualType);
    }

    private OrderResultLists() {}
    private static Map<Class, Class> resultListMap = ResultListUtils.getMap(
            OrderResultList.class);
}

class OrderResultList extends ResultList<Order> {
    @Override
    public List<Order> getItems() {
        return super.getItems();
    }

    @Override
    public void setItems(List<Order> items) {
        super.setItems(items);
    }
}
