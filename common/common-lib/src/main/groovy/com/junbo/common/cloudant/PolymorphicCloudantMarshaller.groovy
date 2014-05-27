/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.cloudant

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.ObjectMapper
import com.junbo.common.cloudant.json.serializer.PolymorphicObjectMapperProvider

/**
 * Created by Zhanxin on 5/26/2014.
 */
class PolymorphicCloudantMarshaller implements CloudantMarshaller {
    private static PolymorphicCloudantMarshaller instance = new PolymorphicCloudantMarshaller(PolymorphicObjectMapperProvider.instance())

    static PolymorphicCloudantMarshaller instance() {
        return instance
    }

    static void setInstance(PolymorphicCloudantMarshaller instance) {
        PolymorphicCloudantMarshaller.instance = instance
    }

    private PolymorphicCloudantMarshaller(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper
    }

    /**
     * static Fastxml jackson ObjectMapper used for marshall and unmarshall.
     */
    private ObjectMapper objectMapper;

    /**
     * Method to marshall an object into a json string.
     * @param object The object to be marshaled.
     * @return The json string of the input object.
     */
    String marshall(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object)
    }

    /**
     * Method to unmarshall a json string to an object of the given class.
     * @param string The json string.
     * @param clazz The target class of the unmarshalled object.
     * @return The unmashalled Object of the given class.
     */
    @Override
    <T> T unmarshall(String string, Class<T> clazz) throws IOException {
        return objectMapper.readValue(string, clazz)
    }

    /**
     * Method to unmashall a json string to an object of a generic class, such as List<String>.
     * @param string The json string.
     * @param parametrized The generic class parametrized part.
     * @param parameterClass The generic class generic part.
     * @return The unmashalled Object of the given generic class.
     */
    @Override
    <T> T unmarshall(String string, Class<?> parametrized, Class<?> parameterClass) throws IOException {
        // Construct the JavaType with the given parametrized class and generic type.
        JavaType javaType = objectMapper.typeFactory.constructParametricType(parametrized, parameterClass)
        return objectMapper.readValue(string, javaType)
    }
}
