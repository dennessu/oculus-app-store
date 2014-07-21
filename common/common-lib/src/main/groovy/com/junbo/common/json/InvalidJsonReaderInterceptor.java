/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.json;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.junbo.common.error.AppCommonErrors;
import org.springframework.util.CollectionUtils;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.ReaderInterceptorContext;
import java.io.IOException;
import java.util.List;

/**
 * Created by liangfu on 3/11/14.
 */
@Provider
public class InvalidJsonReaderInterceptor implements ReaderInterceptor {
    public static final String DEFAULT_FIELD = "request.body";
    @Override
    public Object aroundReadFrom(ReaderInterceptorContext context) throws IOException, WebApplicationException {
        try {
            return context.proceed();
        } catch (InvalidFormatException invalidFormatException) {
            throw AppCommonErrors.INSTANCE.invalidJson(DEFAULT_FIELD, invalidFormatException.getOriginalMessage()).exception();
        } catch (UnrecognizedPropertyException unrecognizedPropertyException) {
            throw AppCommonErrors.INSTANCE.invalidJson(unrecognizedPropertyException.getUnrecognizedPropertyName(), "Unrecognized Property").exception();
        } catch (JsonMappingException jsonMappingException) {
            throw AppCommonErrors.INSTANCE.invalidJson(buildField(jsonMappingException.getPath()), jsonMappingException.getOriginalMessage()).exception();
        } catch (JsonParseException ex) {
            throw AppCommonErrors.INSTANCE.invalidJson(DEFAULT_FIELD, ex.getOriginalMessage()).exception();
        }
    }

    private String buildField(List<JsonMappingException.Reference> paths) {
        if (CollectionUtils.isEmpty(paths)) {
            return DEFAULT_FIELD;
        } else if (paths.size() == 1) {
            return paths.get(0).getFieldName();
        } else {
            StringBuilder sb = new StringBuilder(paths.get(0).getFieldName());
            for (int i=1; i< paths.size(); i++) {
                sb.append(".").append(paths.get(i).getFieldName());
            }
            return sb.toString();
        }
    }
}
