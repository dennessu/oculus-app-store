/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.store.apihelper;

import java.util.HashMap;
import java.util.Map;

/**
 * The TestContext class.
 */
public class TestContext {

    private static final ThreadLocal<TestContextData> CURRENT_DATA = new ThreadLocal<TestContextData>() {
        @Override protected TestContextData initialValue() {
            return new TestContextData();
        }
    };

    /**
     * The TestContext class.
     */
    public static class TestContextData {

        private Map<String, String> headers = new HashMap<>();

        public void putHeader(String key, String value) {
            headers.put(key, value);
        }

        public Map<String, String> getHeaders() {
            return headers;
        }

        public void setHeaders(Map<String, String> headers) {
            this.headers = headers;
        }
    }

    public static TestContextData getData() {
        return CURRENT_DATA.get();
    }

    public static void setData(TestContextData data) {
        CURRENT_DATA.set(data);
    }

    public static void clear() {
        CURRENT_DATA.remove();
    }
}
