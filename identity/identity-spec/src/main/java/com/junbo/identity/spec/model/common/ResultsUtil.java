/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.model.common;

import com.junbo.common.model.Results;

import java.util.List;

/**
 * Created by liangfu on 3/4/14.
 */
public class ResultsUtil {
    private ResultsUtil() {

    }

    public static <T> Results<T> init(List<T> entities, Integer count) {
        Results<T> resultList = new Results<T>();

        if (entities != null && count != null && entities.size() > count) {
            resultList.setHasNext(true);
            resultList.setItems(entities.subList(0, count));
        } else {
            resultList.setHasNext(false);
            resultList.setItems(entities);
        }

        return resultList;
    }
}
