/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.docs;

import com.junbo.common.enumid.EnumId;
import com.junbo.common.id.CloudantId;
import com.junbo.common.id.Id;
import com.junbo.langur.core.promise.Promise;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.core.util.ClassWrapper;
import com.wordnik.swagger.jersey.JerseyApiReader;
import com.wordnik.swagger.model.Operation;
import groovy.lang.MetaClass;
import org.glassfish.hk2.utilities.reflection.ReflectionHelper;

import javax.ws.rs.core.Response;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The hook to change API parameter types when reading API.
 */
public class OculusApiReader extends JerseyApiReader {
    private OculusSamplesReader samplesReader = new OculusSamplesReader();

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
        Class actualParamType = (Class)getParamType(ClassWrapper.apply(paramType));
        Type actualGenericType = getParamType(ClassWrapper.apply(genericParamType));
        return super.processDataType(actualParamType, actualGenericType);
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

    private Type getParamType(ClassWrapper cls) {
        if (MetaClass.class.isAssignableFrom(cls.getRawClass())) {
            return null;
        }
        if (Response.class.isAssignableFrom(cls.getRawClass())) {
            return null;
        }
        if (UUID.class.isAssignableFrom(cls.getRawClass())) {
            return String.class;
        }
        if (BigDecimal.class.isAssignableFrom(cls.getRawClass())) {
            return String.class;
        }
        if (Id.class.isAssignableFrom(cls.getRawClass())) {
            return String.class;
        }
        if (Id.class.isAssignableFrom(cls.getRawClass())) {
            return String.class;
        }
        if (CloudantId.class.isAssignableFrom(cls.getRawClass())) {
            return String.class;
        }
        if (EnumId.class.isAssignableFrom(cls.getRawClass())) {
            return String.class;
        }

        // recursion
        if (cls.getRawClass().isArray()) {
            if (cls.getRawType() instanceof GenericArrayType) {
                return new GenericArrayTypeImpl(safeGetParamType(cls.getArrayComponent()));
            }
            return cls.getRawClass();
        } else if (cls.getRawType() instanceof ParameterizedType) {
            TypeVariable[] types = cls.getRawClass().getTypeParameters();

            List<Type> newTypes = new ArrayList<>();
            for (TypeVariable argType : types) {
                ClassWrapper actualClassWrapper = cls.getTypeArgument(argType.getName());
                Type actualType = safeGetParamType(actualClassWrapper);
                newTypes.add(actualType);
            }
            return new ParameterizedTypeImpl(cls.getRawClass(), newTypes.toArray(new Type[0]));
        } else {
            return cls.getRawClass();
        }
    }

    private Type safeGetParamType(ClassWrapper cls) {
        Type type = getParamType(cls);
        if (type == null) {
            return Object.class;
        }
        return type;
    }
}
