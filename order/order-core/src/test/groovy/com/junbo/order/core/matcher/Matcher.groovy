package com.junbo.order.core.matcher

import org.codehaus.groovy.runtime.DefaultGroovyMethods
import org.easymock.EasyMock
import org.easymock.IArgumentMatcher

/**
 * Created by fzhang on 14-3-10.
 */
class Matcher {

    static <T> T memberEquals(T object) {
        EasyMock.reportMatcher(new IArgumentMatcher() {
            @Override
            boolean matches(Object argument) {
                return objectMemberEquals(object, argument, Collections.singleton(object.getClass()))
            }

            @Override
            void appendTo(StringBuffer buffer) {
                buffer.append('memberEquals(' + object + ')')
            }
        })
        return null
    }

    private static boolean objectMemberEquals(Object left, Object right, Set<Class> includedClass) {
        if (!useMemberEquals(left, includedClass) || !useMemberEquals(right, includedClass)) {
            return left == right
        }
        if (left == right) {
            return true
        }

        Map leftProperties = DefaultGroovyMethods.getProperties(left)
        Map rightProperties = DefaultGroovyMethods.getProperties(right)
        for (Map.Entry<String, Object> entry : leftProperties.entrySet()) {
            if (!objectMemberEquals(entry.value, rightProperties[entry.key], includedClass)) {
                return false
            }
        }
        return true
    }

    private static boolean useMemberEquals(Object obj, Set<Class> includedClass) {
        return obj != null && includedClass.contains(obj.class)
    }
}
