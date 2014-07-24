/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.core.common

import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.ObjectMapper
import groovy.transform.CompileStatic

/**
 * Json Marshaller implementation.
 * Used internally for entity persistence and other.
 */
@CompileStatic
class JsonMarshaller {
    /**
     * static Fastxml jackson ObjectMapper used for marshall and unmarshall.
     */
    private static ObjectMapper objectMapper

    /**
     * Initialize the objectMapper here.
     */
    static {
        objectMapper = new ObjectMapper()
    }

    /**
     * Method to marshall an object into a json string.
     * @param object The object to be marshaled.
     * @return The json string of the input object.
     */
    static String marshall(Object object) {
        return objectMapper.writeValueAsString(object)
    }

    /**
     * Method to unmarshall a json string to an object of the given class.
     * @param string The json string.
     * @param clazz The target class of the unmarshalled object.
     * @return The unmashalled Object of the given class.
     */
    static <T> T unmarshall(String string, Class<T> clazz) {
        return objectMapper.readValue(string, clazz)
    }

    /**
     * Method to unmashall a json string to an object of a generic class, such as List<String>.
     * @param string The json string.
     * @param parametrized The generic class parametrized part.
     * @param parameterClass The generic class generic part.
     * @return The unmashalled Object of the given generic class.
     */
    static <T> T unmarshall(String string, Class<?> parametrized, Class<?>... parameterClass) {
        // Construct the JavaType with the given parametrized class and generic type.
        JavaType javaType = objectMapper.typeFactory.constructParametricType(parametrized, parameterClass)
        return objectMapper.readValue(string, javaType)
    }
}
