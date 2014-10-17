package com.junbo.store.common.utils

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

}