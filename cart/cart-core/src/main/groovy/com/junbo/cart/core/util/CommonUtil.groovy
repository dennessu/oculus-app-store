/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.cart.core.util

import groovy.transform.CompileStatic

/**
 * Created by fzhang@wan-san.com on 14-1-27.
 */
@CompileStatic
class CommonUtil {

    static <K, T> Map<K, T> listToMap(List<T> list, Map<K, T> map, KeyAccessor<K, T> keyAccessor) {
        if (list != null) {
            list.each {
                T item = (T) it
                map[(K) keyAccessor.key(item)] = item
            }
        }
        return map
    }

}
