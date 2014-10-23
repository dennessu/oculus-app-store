package com.junbo.store.common.utils

import com.fasterxml.jackson.databind.util.ISO8601DateFormat
import com.junbo.langur.core.context.JunboHttpContext
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.apache.commons.lang3.StringUtils
import org.apache.http.NameValuePair
import org.apache.http.client.utils.URLEncodedUtils
import org.springframework.util.Assert
import org.springframework.util.CollectionUtils

/**
 * The CommonUtils class.
 */
@CompileStatic
class CommonUtils {

    private static final String AUTH_HEADER = 'Authorization'

    private static final Date DefaultDate = new ISO8601DateFormat().parse('1900-01-01T00:00:00Z')

    public static String allowedEnumValues(Class enumClass) {
        List<String> values = []
        for (Object enumVal : enumClass.enumConstants) {
            values << ((Enum) enumVal).name()
        }
        return "[${StringUtils.join(values, ',')}]"
    }

    public static String getQueryParam(String url, String paramName) {
            if (StringUtils.isEmpty(url)) {
                return null
            }
            NameValuePair cursorPair = URLEncodedUtils.parse(new URI(url), 'UTF-8').find { NameValuePair pair -> pair.name == paramName}
            return cursorPair?.value
    }

    public static Promise<Void> loop(Closure<Promise> func) {
        func.call().then { Object obj ->
            if (obj == Promise.BREAK) {
                return Promise.pure()
            }
            return loop(func)
        }
    }

    public static void pushAuthHeader(String accessToken) {
        JunboHttpContext.requestHeaders.addFirst(AUTH_HEADER, 'Bearer ' + accessToken)
    }

    public static String popAuthHeader() {
        Assert.isTrue(!CollectionUtils.isEmpty(JunboHttpContext.requestHeaders?.get(AUTH_HEADER)))
        return JunboHttpContext.requestHeaders.get(AUTH_HEADER).remove(0)
    }

    public static int safeInt(Number number) {
        return number == null ? 0 : number.intValue()
    }

    public static long safeLong(Number number) {
        return number == null ? 0 : number.longValue()
    }

    public static double safeDouble(Number number) {
        return number == null ? 0 : number.doubleValue()
    }

    public static String toDefaultIfNull(String val) {
        if (val == null) {
            return ''
        }
        return val
    }

    public static Date toDefaultIfNull(Date val) {
        if (val == null) {
            return DefaultDate
        }
        return val
    }

    public static Map toDefaultIfNull(Map val) {
        if (val == null) {
            return [:] as Map
        }
        return val
    }

    public static List toDefaultIfNull(List val) {
        if (val == null) {
            return [] as List
        }
        return val
    }

    public static Integer toDefaultIfNull(Integer val) {
        if (val == null) {
            return 0
        }
        return val
    }

    public static Long toDefaultIfNull(Long val) {
        if (val == null) {
            return 0L
        }
        return val
    }
}