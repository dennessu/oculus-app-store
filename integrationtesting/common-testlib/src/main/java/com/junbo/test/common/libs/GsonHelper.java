/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common;

import com.google.gson.Gson;

import java.io.Reader;

/**
 * @author dw
 */
public class GsonHelper {

    private GsonHelper() {

    }

    public static String GsonSerializer(Object obj) throws Exception {
        Gson gson = new Gson();
        return gson.toJson(obj);
    }

    public static Object GsonDeserializer(Reader reader, Class<?> cls) throws Exception {
        Gson gson = new Gson();
        return gson.fromJson(reader, cls);
    }

}
