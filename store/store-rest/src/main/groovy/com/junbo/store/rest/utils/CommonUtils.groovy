package com.junbo.store.rest.utils

import groovy.transform.CompileStatic
import org.apache.commons.lang3.StringUtils

@CompileStatic
class CommonUtils {

    public static String allowedEnumValues(Class enumClass) {
        List<String> values = []
        for (Object enumVal : enumClass.enumConstants) {
            values << ((Enum) enumVal).name()
        }
        return "[${StringUtils.join(values, ',')}]"
    }
}
