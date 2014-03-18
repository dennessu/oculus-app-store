/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.docs.app.readers;

import com.junbo.common.id.Id;
import com.junbo.common.model.Link;
import com.wordnik.swagger.model.Model;
import com.wordnik.swagger.reader.PropertyMetaInfo;
import scala.Option;
import scala.collection.immutable.Map;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * The model reader to hook for model types.
 */
public class ModelReader implements com.wordnik.swagger.reader.ModelReader {
    @Override
    public Option<Map<String, Model>> read(String modelType) {
        return scala.Option.apply(null);
    }

    @Override
    public PropertyMetaInfo parseMethod(Method method, PropertyMetaInfo metaInfo) {
        if (Id.class.isAssignableFrom(metaInfo.returnClass())) {
            Class refClass = Link.class;
            return new PropertyMetaInfo(
                    refClass,
                    metaInfo.propertyName(),
                    metaInfo.propertyAnnotations(),
                    refClass,
                    refClass);
        } else if (Collection.class.isAssignableFrom(metaInfo.returnClass())) {
            ParameterizedType type = (ParameterizedType)metaInfo.genericReturnType();
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
                    metaInfo.propertyAnnotations(),
                    new ParameterizedTypeImpl(metaInfo.returnClass(), newTypes.toArray(new Type[0])),
                    metaInfo.returnClass());
        }
        return metaInfo;
    }

    @Override
    public PropertyMetaInfo parseField(Field field, PropertyMetaInfo metaInfo) {
        // We not using public fields. This should never be hit.
        assert(false);
        return metaInfo;
    }
}
