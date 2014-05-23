package com.junbo.langur.core.context

import com.google.common.collect.Multimap
import groovy.transform.CompileStatic

/**
 * Created by kg on 5/23/2014.
 */
@CompileStatic
class JunboHttpContext {

    private static final ThreadLocal<JunboHttpContextData> CURRENT_DATA = new ThreadLocal<>()

    static class JunboHttpContextData {

        String requestMethod

        URI requestUri

        Multimap<String, String> requestHeaders

        Integer responseStatus

        Multimap<String, String> responseHeaders
    }

    static String getRequestMethod() {
        def data = CURRENT_DATA.get()
        if (data == null) {
            return null
        }

        return data.requestMethod
    }

    static URI getRequestUri() {
        def data = CURRENT_DATA.get()
        if (data == null) {
            return null
        }

        return data.requestUri
    }

    static Multimap<String, String> getRequestHeaders() {
        def data = CURRENT_DATA.get()
        if (data == null) {
            return null
        }

        return data.requestHeaders
    }

    static Integer getResponseStatus() {
        def data = CURRENT_DATA.get()
        if (data == null) {
            throw new IllegalStateException('Current JunboHttpContextData is null')
        }

        return data.responseStatus
    }

    static void setResponseStatus(Integer status) {
        def data = CURRENT_DATA.get()
        if (data == null) {
            throw new IllegalStateException('Current JunboHttpContextData is null')
        }

        data.responseStatus = status
    }


    static Multimap<String, String> getResponseHeaders() {
        def data = CURRENT_DATA.get()
        if (data == null) {
            throw new IllegalStateException('Current JunboHttpContextData is null')
        }

        return data.responseHeaders
    }

    static JunboHttpContextData getData() {
        return CURRENT_DATA.get()
    }

    static void setData(JunboHttpContextData data) {
        CURRENT_DATA.set(data)
    }
}
