package com.junbo.langur.core.context

import groovy.transform.CompileStatic
import org.glassfish.jersey.internal.util.collection.StringKeyIgnoreCaseMultivaluedMap
import org.springframework.util.CollectionUtils
import org.springframework.util.StringUtils

import javax.ws.rs.core.MultivaluedMap

/**
 * Created by kg on 5/23/2014.
 */
@CompileStatic
class JunboHttpContext {

    private static final ThreadLocal<JunboHttpContextData> CURRENT_DATA = new ThreadLocal<>()

    private static String defaultCountry = 'US'

    private static String defaultLocale = 'en_US'

    private static String localeWildCard = '*'

    static class JunboHttpContextData {

        String requestMethod

        URI requestUri

        MultivaluedMap<String, String> requestHeaders

        Class requestHandler


        Integer responseStatus

        MultivaluedMap<String, String> responseHeaders


        String requestIpAddress

        Map<String, Object> properties

        List<Locale> acceptableLanguages

        JunboHttpContextData() {

            requestHeaders = new StringKeyIgnoreCaseMultivaluedMap<>()

            responseStatus = -1
            responseHeaders = new StringKeyIgnoreCaseMultivaluedMap<>()

            properties = new HashMap<>()
        }
    }

    static String getRequestIpAddress() {
        def data = CURRENT_DATA.get()
        if (data == null) {
            return null
        }

        return data.requestIpAddress
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

    static Class getRequestHandler() {
        def data = CURRENT_DATA.get()
        if (data == null) {
            return null
        }
        return data.requestHandler
    }

    static MultivaluedMap<String, String> getRequestHeaders() {
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


    static MultivaluedMap<String, String> getResponseHeaders() {
        def data = CURRENT_DATA.get()
        if (data == null) {
            throw new IllegalStateException('Current JunboHttpContextData is null')
        }

        return data.responseHeaders
    }

    static Map<String, Object> getProperties() {
        def data = CURRENT_DATA.get()
        if (data == null) {
            throw new IllegalStateException('Current JunboHttpContextData is null')
        }

        return data.properties
    }

    static String getAcceptableLanguage() {
        def data = CURRENT_DATA.get()
        if (data == null) {
            throw new IllegalStateException('Current JunboHttpContextData is null')
        }
        String localeId
        if (CollectionUtils.isEmpty(data.acceptableLanguages)) {
            localeId = defaultLocale
        } else {
            Locale requestLocale = data.acceptableLanguages.get(0)
            if (requestLocale.toString() == localeWildCard) {
                localeId = defaultLocale
            } else {
                localeId = requestLocale.language
                if (!StringUtils.isEmpty(requestLocale.country)) {
                    localeId += "_${requestLocale.country}"
                }
            }
        }

        return localeId
    }

    static JunboHttpContextData getData() {
        return CURRENT_DATA.get()
    }

    static void setData(JunboHttpContextData data) {
        CURRENT_DATA.set(data)
    }
}
