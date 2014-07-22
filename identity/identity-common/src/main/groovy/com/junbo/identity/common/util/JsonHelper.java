/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.common.util;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.junbo.common.error.AppError;
import com.junbo.common.error.ErrorDef;
import com.junbo.common.error.ErrorProxy;
import com.junbo.common.json.ObjectMapperProvider;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * Created by liangfu on 5/5/14.
 */
public class JsonHelper {

    private JsonHelper() {

    }

    public static final String DEFAULT_FIELD = "value";

    public static Object jsonNodeToObj(JsonNode jsonNode, Class cls) {
        try {
            return ObjectMapperProvider.instance().treeToValue(jsonNode, cls);
        } catch (InvalidFormatException invalidFormatException) {
            throw ERRORS.invalidJson(invalidFormatException.getOriginalMessage(), DEFAULT_FIELD).exception();
        } catch (UnrecognizedPropertyException unrecognizedPropertyException) {
            throw ERRORS.invalidJson("unrecognized property",
                    unrecognizedPropertyException.getUnrecognizedPropertyName()).exception();
        } catch (JsonMappingException jsonMappingException) {
            throw ERRORS.invalidJson(jsonMappingException.getOriginalMessage(),
                    buildField(jsonMappingException.getPath())).exception();
        } catch (JsonParseException ex) {
            throw ERRORS.invalidJson(ex.getOriginalMessage(), DEFAULT_FIELD).exception();
        } catch (JsonProcessingException json) {
            throw ERRORS.invalidJson(json.getOriginalMessage(), DEFAULT_FIELD).exception();
        }
    }

    public static final Errors ERRORS = ErrorProxy.newProxyInstance(Errors.class);

    /**
     * Created by liangfu on 5/5/14.
     */
    public interface Errors {

        @ErrorDef(httpStatusCode = 400, code = "501", message = "invalid Json: {0}", field = "{1}")
        AppError invalidJson(String detail, String field);
    }

    private static String buildField(List<JsonMappingException.Reference> paths) {
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
