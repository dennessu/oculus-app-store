/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.common

import com.fasterxml.jackson.databind.ObjectMapper
import groovy.transform.CompileStatic

// TODO:  this is only a temporary implementation
/**
 * Javadoc.
 */
@CompileStatic
class JsonMarshaller {
    private static ObjectMapper objectMapper
    static {
        objectMapper = new ObjectMapper()
    }

    static String marshall(Object object) {
        return objectMapper.writeValueAsString(object)
    }

    static <T> T unmarshall(Class<T> clazz, String string) {
        return objectMapper.readValue(string, clazz)
    }
}
