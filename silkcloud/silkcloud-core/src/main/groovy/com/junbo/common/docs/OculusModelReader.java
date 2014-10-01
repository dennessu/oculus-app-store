/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.docs;

import com.fasterxml.jackson.databind.JsonNode;
import com.junbo.common.enumid.EnumId;
import com.junbo.common.id.CloudantId;
import com.junbo.common.id.Id;
import com.junbo.common.model.Link;
import com.wordnik.swagger.core.util.ClassWrapper;
import com.wordnik.swagger.model.Model;
import com.wordnik.swagger.model.ModelProperty;
import com.wordnik.swagger.model.ModelRef;
import com.wordnik.swagger.reader.PropertyMetaInfo;
import groovy.lang.MetaClass;
import scala.Option;
import scala.collection.immutable.Map;

import javax.ws.rs.core.Response;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The model reader to hook for model types.
 */
public class OculusModelReader implements com.wordnik.swagger.reader.ModelReader {
    @Override
    public Option<Map<String, Model>> read(String modelType) {
        return scala.Option.apply(null);
    }

    @Override
    public PropertyMetaInfo parseMethod(ClassWrapper clazz, Method method, PropertyMetaInfo metaInfo) {

        Type newType = getType(metaInfo.returnClass());
        if (newType == null) {
            return null;
        }

        return new PropertyMetaInfo(
                new ClassWrapper(newType, null),
                metaInfo.propertyName(),
                metaInfo.propertyAnnotations());
    }

    @Override
    public PropertyMetaInfo parseField(ClassWrapper clazz, Field field, PropertyMetaInfo metaInfo) {
        // We not using public fields. This should never be hit.
        assert(false);
        return metaInfo;
    }

    @Override
    public ModelProperty processModelProperty(ModelProperty modelProperty, ClassWrapper cls,
            Annotation[] propertyAnnotations, Annotation[] fieldAnnotations) {
        if (hasIdAnnotation(propertyAnnotations) || hasIdAnnotation(fieldAnnotations)) {
            if (!modelProperty.items().equals(Option.apply(null))) {
                // Convert to Array[Link]
                return new ModelProperty(
                        List.class.getSimpleName(),
                        List.class.getName(),
                        modelProperty.position(),
                        modelProperty.required(),
                        modelProperty.description(),
                        modelProperty.allowableValues(),
                        Option.apply(new ModelRef(
                                null,
                                Option.apply(Link.class.getSimpleName()),
                                Option.apply(Link.class.getName()))));
            }
            return new ModelProperty(
                    Link.class.getSimpleName(),
                    Link.class.getName(),
                    modelProperty.position(),
                    modelProperty.required(),
                    modelProperty.description(),
                    modelProperty.allowableValues(),
                    modelProperty.items()
            );
        }
        return modelProperty;
    }

    private boolean hasIdAnnotation(Annotation[] annotations) {
        if (annotations == null) return false;
        for (Annotation annotation : annotations) {
            String annotationClassName = annotation.annotationType().getName();
            if (annotationClassName.startsWith("com.junbo.common.jackson.annotation.") &&
                annotationClassName.endsWith("Id")) {
                return true;
            }
        }
        return false;
    }

    private Type getType(ClassWrapper cls) {
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
            return Link.class;
        }
        if (CloudantId.class.isAssignableFrom(cls.getRawClass())) {
            return Link.class;
        }
        if (EnumId.class.isAssignableFrom(cls.getRawClass())) {
            return Link.class;
        }
        if (JsonNode.class.isAssignableFrom(cls.getRawClass())) {
            return Object.class;
        }

        // recursion
        if (cls.getRawClass().isArray()) {
            if (cls.getRawType() instanceof GenericArrayType) {
                return new GenericArrayTypeImpl(safeGetType(cls.getArrayComponent()));
            }
            return cls.getRawClass();
        } else if (cls.getRawType() instanceof ParameterizedType) {
            TypeVariable[] types = cls.getRawClass().getTypeParameters();

            List<Type> newTypes = new ArrayList<>();
            for (TypeVariable argType : types) {
                ClassWrapper actualClassWrapper = cls.getTypeArgument(argType.getName());
                Type actualType = safeGetType(actualClassWrapper);
                newTypes.add(actualType);
            }
            return new ParameterizedTypeImpl(cls.getRawClass(), newTypes.toArray(new Type[0]));
        } else {
            return cls.getRawClass();
        }
    }

    private Type safeGetType(ClassWrapper cls) {
        Type type = getType(cls);
        if (type == null) {
            return Object.class;
        }
        return type;
    }
}
