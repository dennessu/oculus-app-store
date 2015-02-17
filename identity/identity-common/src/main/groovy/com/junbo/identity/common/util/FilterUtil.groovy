/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.common.util

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
}
