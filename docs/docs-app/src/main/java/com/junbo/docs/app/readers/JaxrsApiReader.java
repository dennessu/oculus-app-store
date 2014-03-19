/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.docs.app.readers;

import com.junbo.common.id.Id;
import com.junbo.docs.app.resultlists.IdentityResultLists;
import com.junbo.langur.core.promise.Promise;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.model.Operation;
import org.glassfish.hk2.utilities.reflection.ReflectionHelper;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * The hook to change API parameter types when reading API.
 */
public class JaxrsApiReader extends com.wordnik.swagger.jaxrs.reader.DefaultJaxrsApiReader {
    @Override
    public Operation processOperation(Operation operation, Method method, ApiOperation apiOperation) {
        String responseClass = operation.responseClass();

        Type actualType = getActualType(method.getGenericReturnType());

        if (actualType != method.getGenericReturnType()) {
            // the type is replaced by something else, re-generate the Operation
            assert(actualType instanceof Class);
            responseClass = ((Class)actualType).getName();
            return new Operation(
                    operation.method(),
                    operation.summary(),
                    operation.notes(),
                    responseClass,
                    operation.nickname(),
                    operation.position(),
                    operation.produces(),
                    operation.consumes(),
                    operation.protocols(),
                    operation.authorizations(),
                    operation.parameters(),
                    operation.responseMessages(),
                    operation.deprecated());
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
        } else if (clazz.equals(com.junbo.identity.spec.model.common.ResultList.class)) {
            ParameterizedType parameterizedType = (ParameterizedType)wrapperType;
            Type actualType = IdentityResultLists.getClass(parameterizedType);

            return getActualType(actualType);
        } else {
            return wrapperType;
        }
    }
}
