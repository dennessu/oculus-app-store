package com.junbo.apphost.core.logging

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.pattern.CompositeConverter
import groovy.transform.CompileStatic

/**
 * Created by kg on 4/21/2014.
 */
@CompileStatic
@SuppressWarnings('UnnecessaryGetter')
class ColorConverter extends CompositeConverter<ILoggingEvent> {

    private static final Map<String, AnsiElement> ELEMENTS

    static {
        Map<String, AnsiElement> elements = new HashMap<String, AnsiElement>()

        elements.put('faint', AnsiElement.FAINT)
        elements.put('red', AnsiElement.RED)
        elements.put('green', AnsiElement.GREEN)
        elements.put('yellow', AnsiElement.YELLOW)
        elements.put('blue', AnsiElement.BLUE)
        elements.put('magenta', AnsiElement.MAGENTA)
        elements.put('cyan', AnsiElement.CYAN)
        elements.put('white', AnsiElement.WHITE)
        elements.put('bold', AnsiElement.BOLD)

        ELEMENTS = Collections.unmodifiableMap(elements)
    }

    private static final Map<Integer, AnsiElement> LEVELS

    static {
        Map<Integer, AnsiElement> levels = new HashMap<Integer, AnsiElement>()

        levels.put(Level.ERROR_INTEGER, AnsiElement.RED)
        levels.put(Level.WARN_INTEGER, AnsiElement.YELLOW)

        LEVELS = Collections.unmodifiableMap(levels)
    }

    @Override
    protected String transform(ILoggingEvent event, String input) {

        AnsiElement element = ELEMENTS.get(getFirstOption())

        if (element == null) {
            // Assume highlighting
            element = LEVELS.get(event.level.toInteger())
            element = (element == null ? AnsiElement.GREEN : element)
        }

        return AnsiOutput.toString(element, input)
    }
}
