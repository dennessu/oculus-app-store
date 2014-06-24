/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.cloudant.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.util.VersionUtil;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.junbo.common.cloudant.CloudantEntity;
import com.junbo.common.cloudant.json.annotations.*;

import java.lang.annotation.Annotation;

/**
 * The cloudant annotation introspector which supports some basic annotations.
 * <p/>
 * Now we only support the following annotations:
 *  CloudantIgnore,
 *  CloudantSerializer,
 *  CloudantDeserializer,
 *  CloudantAnnotationsInside,
 *  CloudantProperty
 */
public class CloudantAnnotationIntrospector extends AnnotationIntrospector {

    private static final Version VERSION = VersionUtil.parseVersion("0.0.1", "com.junbo", "cloudant-databind");

    @Override
    public Version version() {
        return VERSION;
    }

    @Override
    public boolean isAnnotationBundle(Annotation annotation) {
        return annotation.annotationType().getAnnotation(CloudantAnnotationsInside.class) != null;
    }

    @Override
    public boolean hasIgnoreMarker(AnnotatedMember member) {
        CloudantIgnore ignore = member.getAnnotation(CloudantIgnore.class);
        if (ignore != null || isSelfProperty(member)) {
            return true;
        }

        if (member.getRawType() != null &&
            member.getRawType().getAnnotation(CloudantIgnore.class) != null) {
            return true;
        }
        return false;
    }

    @Override
    public Object findSerializer(Annotated annotated) {
        CloudantSerialize annotation = annotated.getAnnotation(CloudantSerialize.class);
        if (annotation != null) {
            Class<? extends JsonSerializer<?>> serializerCls = annotation.value();
            if (serializerCls != JsonSerializer.None.class) {
                return serializerCls;
            }
        }
        return null;
    }

    @Override
    public PropertyName findNameForSerialization(Annotated annotated) {
        CloudantProperty annotation = annotated.getAnnotation(CloudantProperty.class);
        if (annotation != null) {
            return new PropertyName(annotation.value());
        }

        return null;
    }

    @Override
    public Class<? extends JsonDeserializer<?>> findDeserializer(Annotated annotated) {
        CloudantDeserialize annotation = annotated.getAnnotation(CloudantDeserialize.class);
        if (annotation != null) {
            Class<? extends JsonDeserializer<?>> deserializerCls = annotation.value();
            if (deserializerCls != JsonDeserializer.None.class) {
                return deserializerCls;
            }
        }

        return null;
    }

    @Override
    public PropertyName findNameForDeserialization(Annotated annotated) {
        CloudantProperty annotation = annotated.getAnnotation(CloudantProperty.class);
        if (annotation != null) {
            return new PropertyName(annotation.value());
        }

        return null;
    }

    private boolean isSelfProperty(Annotated annotated) {
        if (annotated instanceof AnnotatedField) {
            AnnotatedField field = (AnnotatedField)annotated;
            if (CloudantEntity.class.isAssignableFrom(field.getDeclaringClass())) {
                JsonProperty annotation = annotated.getAnnotation(JsonProperty.class);
                if (annotation != null && "self".equals(annotation.value())) {
                    return true;
                }
            }
        }
        return false;
    }
}
