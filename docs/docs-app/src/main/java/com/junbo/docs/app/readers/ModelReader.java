/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.docs.app.readers;

import com.junbo.common.id.Id;
import com.junbo.common.model.Link;
import com.wordnik.swagger.core.util.ClassWrapper;
import com.wordnik.swagger.model.Model;
import com.wordnik.swagger.reader.PropertyMetaInfo;
import groovy.lang.MetaClass;
import scala.Option;
import scala.collection.immutable.Map;

import javax.ws.rs.core.Response;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * The model reader to hook for model types.
 */
public class ModelReader implements com.wordnik.swagger.reader.ModelReader {
    @Override
    public Option<Map<String, Model>> read(String modelType) {
        return scala.Option.apply(null);
    }

    @Override
    public PropertyMetaInfo parseMethod(ClassWrapper clazz, Method method, PropertyMetaInfo metaInfo) {
        if (MetaClass.class.isAssignableFrom(metaInfo.returnClass().getRawClass())) {
            return null;
        }
        if (Response.class.isAssignableFrom(metaInfo.returnClass().getRawClass())) {
            return null;
        }
        if (UUID.class.isAssignableFrom(metaInfo.returnClass().getRawClass())) {
            return new PropertyMetaInfo(
                    new ClassWrapper(String.class, null),
                    metaInfo.propertyName(),
                    metaInfo.propertyAnnotations());
        }
        if (BigDecimal.class.isAssignableFrom(metaInfo.returnClass().getRawClass())) {
            return new PropertyMetaInfo(
                    new ClassWrapper(String.class, null),
                    metaInfo.propertyName(),
                    metaInfo.propertyAnnotations());
        }
        if (Id.class.isAssignableFrom(metaInfo.returnClass().getRawClass())) {
            Class refClass = Link.class;
            return new PropertyMetaInfo(
                    new ClassWrapper(refClass, null),
                    metaInfo.propertyName(),
                    metaInfo.propertyAnnotations());
        } else if (Collection.class.isAssignableFrom(metaInfo.returnClass().getRawClass())) {
            if (metaInfo.returnClass().getRawType() instanceof ParameterizedType) {
                ParameterizedType type = (ParameterizedType)metaInfo.returnClass().getRawType();
                Type[] types = type.getActualTypeArguments();

                List<Type> newTypes = new ArrayList<Type>();
                for (Type argType : types) {
                    if (argType instanceof Class && Id.class.isAssignableFrom((Class)argType)) {
                        newTypes.add(Link.class);
                    } else {
                        newTypes.add(argType);
                    }
                }

                return new PropertyMetaInfo(
                        metaInfo.returnClass(),
                        metaInfo.propertyName(),
                        metaInfo.propertyAnnotations());
            }
        }
        return metaInfo;
    }

    @Override
    public PropertyMetaInfo parseField(ClassWrapper clazz, Field field, PropertyMetaInfo metaInfo) {
        // We not using public fields. This should never be hit.
        assert(false);
        return metaInfo;
    }
}
