/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.common.util;

import com.junbo.common.cloudant.DefaultCloudantMarshaller;

import java.io.IOException;

/**
 * Utils.
 */
public class Utils {

    private Utils() {}

    public static String toJson(Object obj) throws IOException {
        return DefaultCloudantMarshaller.instance().marshall(obj);
    }

    public static <T> T toObject(String json, Class<T> clazz) throws IOException {
        return DefaultCloudantMarshaller.instance().unmarshall(json, clazz);
    }
}
