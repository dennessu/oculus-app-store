package com.junbo.store.common.utils

import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.apache.commons.lang3.StringUtils

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

    public static Promise<Void> loop(Closure<Promise> func) {
        func.call().then { Object obj ->
            if (obj == Promise.BREAK) {
                return Promise.pure()
            }
            return loop(func)
        }
    }
}