/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.id.provider;

import com.junbo.common.id.Id;
import com.junbo.common.id.converter.IdTypeFromStringConverter;
import com.junbo.common.util.IdFormatter;
import org.glassfish.jersey.internal.inject.Custom;

import javax.inject.Singleton;
import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import javax.ws.rs.ext.Provider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
        
/**
  * Created by liangfu on 3/7/14.
  */
@Custom
@Singleton
@Provider
public class IdTypeFromStringProvider implements ParamConverterProvider {
    @Override
    public <T> ParamConverter<T> getConverter(final Class<T> rawType, Type genericType, Annotation[] annotations) {
        return Id.class.isAssignableFrom(rawType) ? new IdTypeFromStringConverter.AbstractStringReader<T>() {
            @Override
            protected T _fromString(String value) throws Exception {
                T obj = rawType.newInstance();
                if(!(obj instanceof Id)) {
                    return null;
                }
                ((Id)obj).setValue(IdFormatter.decodeId(rawType, value));
                return obj;
            }
        } : null;
    }
}
