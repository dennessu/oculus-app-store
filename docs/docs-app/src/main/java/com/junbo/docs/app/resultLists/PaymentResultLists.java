/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.docs.app.resultlists;

import com.junbo.payment.spec.model.PaymentInstrument;
import com.junbo.payment.spec.model.PaymentInstrumentType;
import com.junbo.payment.spec.model.ResultList;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * The non-generic ResultList types for identity.
 */
public class PaymentResultLists {

    /**
     * Find the non-generic ResultList type.
     */
    public static Class getClass(ParameterizedType type) {
        Type actualType = type.getActualTypeArguments()[0];
        return resultListMap.get(actualType);
    }

    private PaymentResultLists() {}
    private static Map<Class, Class> resultListMap = ResultListUtils.getMap(
            PaymentInstrumentResultList.class,
            PaymentInstrumentTypeResultList.class);
}

class PaymentInstrumentResultList extends ResultList<PaymentInstrument> {
    @Override
    public List<PaymentInstrument> getResults() {
        return super.getResults();
    }

    @Override
    public void setResults(List<PaymentInstrument> results) {
        super.setResults(results);
    }
}
class PaymentInstrumentTypeResultList extends ResultList<PaymentInstrumentType> {
    @Override
    public List<PaymentInstrumentType> getResults() {
        return super.getResults();
    }

    @Override
    public void setResults(List<PaymentInstrumentType> results) {
        super.setResults(results);
    }
}
