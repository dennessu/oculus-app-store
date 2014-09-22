package com.junbo.store.common.utils

import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.apache.commons.lang3.StringUtils
import org.apache.http.NameValuePair
import org.apache.http.client.utils.URLEncodedUtils

/**
 * The CommonUtils class.
 */
@CompileStatic
class CommonUtils {

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

    public static int safeInt(Number number) {
        return number == null ? 0 : number.intValue()
    }

    public static long safeLong(Number number) {
        return number == null ? 0 : number.longValue()
    }

}