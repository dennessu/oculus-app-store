/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.id.converter;

import org.glassfish.jersey.internal.inject.Custom;
import org.glassfish.jersey.server.internal.inject.ExtractorException;
import javax.inject.Singleton;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.ParamConverter;
import java.lang.reflect.InvocationTargetException;
        
/**
  * Created by liangfu on 3/7/14.
  */
@Custom
@Singleton
public class IdTypeFromStringConverter {
    /**
      * Java doc for reader.
      * @param <T>
      */
    public abstract static class AbstractStringReader<T> implements ParamConverter<T> {
        @Override
        public T fromString(String value) {
            try {
                return _fromString(value);
            }
            catch (InvocationTargetException ex) {
                // if the value is an empty string, return null
                if (value.isEmpty()) {
                    return null;
                }
                Throwable cause = ex.getCause();
                if (cause instanceof WebApplicationException) {
                    throw (WebApplicationException) cause;
                }
                else {
                    throw new ExtractorException(cause);
                }
            }
            catch (Exception ex) {
                throw new ProcessingException(ex);
            }
        }

        protected abstract T _fromString(String value) throws Exception;

        @Override
        public String toString(T value) throws IllegalArgumentException {
            return value.toString();
        }
    }
}
