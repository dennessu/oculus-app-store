/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.authorization.filter

import com.junbo.common.json.PropertyAssignedAware
import com.junbo.oom.core.filter.PropertyMappingEvent
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
                type == UUID
    }

    static boolean isPropertyAssignedAwareType(Class type) {
        return PropertyAssignedAware.isAssignableFrom(type)
    }

    /**
     * Initialize the instance with type cls for further object mapping.
     * If defaultValue exists and the object is not property assigned aware,
     * use the defaultValue rather then creating a clean initial object.
     */
    static void initComplexProperty(PropertyMappingEvent event) {
        Class cls = event.sourcePropertyType;
        Object alternativeSourceProperty = event.alternativeSourceProperty;
        if (alternativeSourceProperty != null) {
            if (!FilterUtil.isPropertyAssignedAwareType(cls)) {
                event.sourceProperty = alternativeSourceProperty
                // alternativeSourceProperty should be treated null to prevent futher mapping
                event.alternativeSourceProperty = null
            } else {
                event.sourceProperty = initInstance(cls)
            }
        }
    }

    private static Object initInstance(Class cls) {
        if (cls == Map.class) {
            return new HashMap()
        } else if (cls == List.class) {
            return new ArrayList()
        } else {
            return cls.newInstance()
        }
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
