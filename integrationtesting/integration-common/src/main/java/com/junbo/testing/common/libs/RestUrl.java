package com.junbo.testing.common.libs;

/**
 * Created by jiefeng on 14-3-19.
 */
public final class RestUrl {
    public static String requestHeaderName = "Content-Type";
    public static String requestHeaderValue = "application/json";

    private RestUrl(){
    }

    public static String getRestUrl(String componentName){

        return "http://" +
                ConfigPropertiesHelper.instance().getProperty(componentName + ".host") +
                ":" +
                ConfigPropertiesHelper.instance().getProperty(componentName + ".port") +
                "/rest/";
    }
}
