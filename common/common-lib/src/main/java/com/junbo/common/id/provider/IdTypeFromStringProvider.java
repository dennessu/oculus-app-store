/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.id.provider;

import com.junbo.common.id.Id;
import com.junbo.common.id.OrderId;
import com.junbo.common.id.converter.IdTypeFromStringConverter;
import com.junbo.common.shuffle.Oculus40Id;
import com.junbo.common.shuffle.Oculus48Id;
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
        return (rawType.getSuperclass() != Id.class) ? null : new IdTypeFromStringConverter.AbstractStringReader<T>() {
            @Override
            protected T _fromString(String value) throws Exception {
                T obj = rawType.newInstance();
                if(obj instanceof OrderId) {
                    Long deFormatValue = Oculus40Id.deFormat(value);
                    ((OrderId) obj).setValue(Oculus40Id.unShuffle(deFormatValue));
                }
                if(obj instanceof Id) {
                    Long deFormatValue = Oculus48Id.deFormat(value);
                    ((Id) obj).setValue(Oculus48Id.unShuffle(deFormatValue));
                    return obj;
                }
                return null;
            }
        };
    }
}
