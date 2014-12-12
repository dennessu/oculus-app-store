/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.authorization.filter

import com.fasterxml.jackson.databind.JsonNode
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 3/26/14.
 */
@CompileStatic
class FilterUtil {
    static boolean isSimpleType(Class type) {
        return type == Boolean ||
                type == String ||
                type == Date ||
                Number.isAssignableFrom(type) ||
                type == UUID ||
                type == JsonNode
    }

    static boolean equalsSimpleType(Object obj1, Object obj2) {
        if (obj1 == null && obj2 == null) {
            return true
        }

        if (obj1 == null && obj2 != null) {
            return false
        }

        if (obj1 != null && obj2 == null) {
            return false
        }

        if (obj1.class != obj2.class) {
            return false
        }

        if (obj1 instanceof Date) {
            def time1 = (long) (((Date) obj1).time / 1000)
            def time2 = (long) (((Date) obj2).time / 1000)

            return time1 == time2
        }

        return obj1.equals(obj2)
    }
}
