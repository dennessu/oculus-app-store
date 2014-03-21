/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.testing.common.libs;

/**
 * Created by jiefeng on 14-3-19.
 */
public final class RestUrl {
    public static String requestHeaderName = "Content-Type";
    public static String requestHeaderValue = "application/json";

    private RestUrl() {
    }

    public enum ComponentName {
        OAUTH("oauth"),
        IDENTITY("user"),
        CART("cart"),
        CATALOG("catalog"),
        ORDER("order");

        private String name;

        private ComponentName(String name) {
            this.name = name;
        }

        public String getComponentName() {
            return name;
        }
    }


    public static String getRestUrl(String componentName) {

        return "http://" +
                ConfigPropertiesHelper.instance().getProperty(componentName + ".host") +
                ":" +
                ConfigPropertiesHelper.instance().getProperty(componentName + ".port") +
                "/rest/";
    }

    public static String getRestUrl(ComponentName componentName) {

        return "http://" +
                ConfigPropertiesHelper.instance().getProperty(componentName.getComponentName() + ".host") +
                ":" +
                ConfigPropertiesHelper.instance().getProperty(componentName.getComponentName() + ".port") +
                "/rest/";
    }


}
