/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common.libs;

import com.junbo.test.common.ConfigHelper;

/**
 * Created by jiefeng on 14-3-19.
 */
public final class RestUrl {
    public static String requestHeaderName = "Content-Type";
    public static String requestHeaderValue = "application/json";

    private RestUrl() {
    }


    /**
     * Enum for Component name.
     *
     * @author Yunlongzhao
     */
    public enum ComponentName {
        IDENTITY("identity"),
        CATALOG("catalog"),
        COMMERCE("commerce");

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
                ConfigHelper.getSetting(componentName + ".host") +
                ":" +
                ConfigHelper.getSetting(componentName + ".port") +
                "/rest/";
    }

    public static String getRestUrl(ComponentName componentName) {

        return "http://" +
                ConfigHelper.getSetting(componentName.getComponentName() + ".host") +
                ":" +
                ConfigHelper.getSetting(componentName.getComponentName() + ".port") +
                "/v1/";
    }


}
