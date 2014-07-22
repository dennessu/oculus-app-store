/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.junbo.common.cloudant.model.CloudantQueryResult;
import com.junbo.common.error.AppCommonErrors;
import com.junbo.common.error.AppError;
import com.junbo.common.error.ErrorDetail;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Utility class.
 */
public class Utils {
    private Utils() {
    }

    public static Date now() {
        return new Date();
    }

    public static Long currentTimestamp() {
        return System.currentTimeMillis();
    }

    public static Date maxDate() {
        return new Date(253373469595309L);
    }

    public static Date minDate() {
        return new Date(0);
    }

    public static String toJson(Object input) {
        return JSON.toJSONString(input);
    }

    public static String toJsonWithType(Object input) {
        return JSON.toJSONString(input, SerializerFeature.WriteClassName);
    }

    public static <T> T fromJson(String string, Class<T> clazz){
        return JSON.parseObject(string, clazz);
    }

    public static AppError invalidFields(List<AppError> appErrors) {
        List<ErrorDetail> errorDetails = new ArrayList<>();
        for (AppError error : appErrors) {
            errorDetails.addAll(error.error().getDetails());
        }
        return AppCommonErrors.INSTANCE.fieldInvalid(errorDetails.toArray(new ErrorDetail[0]));
    }

    public static <T>  List<T> getDocs(List<CloudantQueryResult.ResultObject> resultObjects) {
        List<T> results = new ArrayList<>();
        if (resultObjects != null) {
            for (CloudantQueryResult.ResultObject object : resultObjects) {
                results.add((T)object.getDoc());
            }
        }

        return results;
    }

    public static boolean isValidMd5(String value) {
        if (StringUtils.isEmpty(value)) {
            return false;
        }
        return value.matches("[a-fA-F0-9]{32}");
    }
}
