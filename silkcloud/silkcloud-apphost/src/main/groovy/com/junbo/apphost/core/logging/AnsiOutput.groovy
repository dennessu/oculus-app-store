package com.junbo.apphost.core.logging

import groovy.transform.CompileStatic

/**
 * Created by kg on 4/21/2014.
 */
@CompileStatic
abstract class AnsiOutput {

    private static final String ENCODE_JOIN = ';'

    private static final String ENCODE_START = '\033['

    private static final String ENCODE_END = 'm'

    private final static String RESET = '0;' + AnsiElement.DEFAULT

    static String toString(Object... elements) {

        StringBuilder sb = new StringBuilder()

        buildEnabled(sb, elements)
        return sb.toString()
    }

    private static void buildEnabled(StringBuilder sb, Object[] elements) {

        boolean writingAnsi = false
        boolean containsEncoding = false

        for (Object element : elements) {
            if (element instanceof AnsiElement) {
                containsEncoding = true

                if (writingAnsi) {
                    sb.append(ENCODE_JOIN)
                } else {
                    sb.append(ENCODE_START)
                    writingAnsi = true
                }
            } else {
                if (writingAnsi) {
                    sb.append(ENCODE_END)
                    writingAnsi = false
                }
            }

            sb.append(element)
        }

        if (containsEncoding) {
            sb.append(writingAnsi ? ENCODE_JOIN : ENCODE_START)
            sb.append(RESET)
            sb.append(ENCODE_END)
        }
    }
}
