/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.docs.app.readers;

import com.junbo.common.id.Id;
import com.junbo.langur.core.promise.Promise;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.core.util.ClassWrapper;
import com.wordnik.swagger.model.Operation;
import org.glassfish.hk2.utilities.reflection.ReflectionHelper;

import javax.ws.rs.core.Response;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * The hook to change API parameter types when reading API.
 */
public class JaxrsApiReader extends com.wordnik.swagger.jaxrs.reader.DefaultJaxrsApiReader {
    private SamplesReader samplesReader = new SamplesReader();

    @Override
    public Operation processOperation(String endpoint, Operation operation, Method method, ApiOperation apiOperation) {
        Type actualType = getActualType(method.getGenericReturnType());
        if (actualType != method.getGenericReturnType()) {
            return new Operation(
                    operation.method(),
                    operation.summary(),
                    operation.notes(),
                    ClassWrapper.apply(actualType).getName(),
                    operation.nickname(),
                    operation.position(),
                    operation.produces(),
                    operation.consumes(),
                    operation.protocols(),
                    operation.authorizations(),
                    operation.parameters(),
                    operation.responseMessages(),
                    operation.deprecated(),
                    samplesReader.readSamples(operation.method(), endpoint));
        }
        return operation;
    }

    @Override
    public String processDataType(Class<?> paramType, Type genericParamType) {
        if (Id.class.isAssignableFrom(paramType)) {
            // simply replace with string, since it's in parameters
            return "string";
        }
        return super.processDataType(paramType, genericParamType);
    }

    private Type getActualType(Type wrapperType) {
        Class clazz = ReflectionHelper.getRawClass(wrapperType);
        if (clazz.equals(Promise.class)) {
            ParameterizedType parameterizedType = (ParameterizedType)wrapperType;
            Type actualType = parameterizedType.getActualTypeArguments()[0];

            return getActualType(actualType);
        } else if (clazz.equals(Response.class)) {
            return void.class;
        } else {
            return wrapperType;
        }
    }
}
