/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.model.common;

import java.util.List;

/**
 * Created by liangfu on 3/4/14.
 */
public class ResultListUtil {

    public static <T> ResultList<T> init(List<T> entities, Integer count) {
        ResultList<T> resultList = new ResultList<T>();
        resultList.setItems(entities);
        resultList.setHasNext(count == null ? false : (count == (entities == null ? 0 : entities.size())));

        return resultList;
    }
}
