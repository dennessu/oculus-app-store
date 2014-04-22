package com.junbo.apphost.core.logging

import groovy.transform.CompileStatic

/**
 * Created by kg on 4/21/2014.
 */
@CompileStatic
interface AnsiElement {

    @Override
    String toString()

    static final AnsiElement NORMAL = new DefaultAnsiElement('0')

    static final AnsiElement BOLD = new DefaultAnsiElement('1')

    static final AnsiElement FAINT = new DefaultAnsiElement('2')

    static final AnsiElement ITALIC = new DefaultAnsiElement('3')

    static final AnsiElement UNDERLINE = new DefaultAnsiElement('4')

    static final AnsiElement BLACK = new DefaultAnsiElement('30')

    static final AnsiElement RED = new DefaultAnsiElement('31')

    static final AnsiElement GREEN = new DefaultAnsiElement('32')

    static final AnsiElement YELLOW = new DefaultAnsiElement('33')

    static final AnsiElement BLUE = new DefaultAnsiElement('34')

    static final AnsiElement MAGENTA = new DefaultAnsiElement('35')

    static final AnsiElement CYAN = new DefaultAnsiElement('36')

    static final AnsiElement WHITE = new DefaultAnsiElement('37')

    static final AnsiElement DEFAULT = new DefaultAnsiElement('39')

    @CompileStatic
    private static class DefaultAnsiElement implements AnsiElement {

        private final String code

        DefaultAnsiElement(String code) {
            this.code = code
        }

        @Override
        String toString() {
            return code
        }
    }
}
