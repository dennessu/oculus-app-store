package com.junbo.apphost.core.logging
import ch.qos.logback.classic.pattern.ThrowableHandlingConverter
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.classic.spi.IThrowableProxy
import ch.qos.logback.classic.spi.StackTraceElementProxy
import ch.qos.logback.classic.spi.ThrowableProxyUtil
import ch.qos.logback.core.CoreConstants
import groovy.transform.CompileStatic

/**
 * Created by kg on 4/21/2014.
 */
@CompileStatic
class WhitespaceThrowableProxyConverter extends ThrowableHandlingConverter {
    private static final int BUILDER_CAPACITY = 2048;
    private ArrayList<String> ignoredExceptionPackages = new ArrayList<>()

    @Override
    public void start() {
        final List optionList = getOptionList();

        if (optionList != null) {
            for (String option : optionList) {
                ignoredExceptionPackages.add("at " + option + ".");
            }
        }
        super.start();
    }

    @Override
    public String convert(ILoggingEvent event) {
        IThrowableProxy tp = event.getThrowableProxy();
        if (tp == null) {
            return CoreConstants.EMPTY_STRING;
        }

        return throwableProxyToString(tp);
    }

    @Override
    protected String throwableProxyToString(IThrowableProxy tp) {
        StringBuilder result = new StringBuilder(BUILDER_CAPACITY)
        result.append(CoreConstants.LINE_SEPARATOR)
        printThrowable(result, tp, 1)
        result.append(CoreConstants.LINE_SEPARATOR)
        return result.toString()
    }

    private void printThrowable(StringBuilder sb, IThrowableProxy tp, int indent) {
        // print root cause first!
        if (tp.getCause() != null) {
            printThrowable(sb, tp.getCause(), indent);
        }

        ThrowableProxyUtil.subjoinFirstLineRootCauseFirst(sb, tp);
        sb.append(CoreConstants.LINE_SEPARATOR);
        StackTraceElementProxy[] stepArray = tp.getStackTraceElementProxyArray();
        int commonFrames = tp.getCommonFrames();

        int maxIndex = stepArray.length;
        if (commonFrames > 0) {
            maxIndex -= commonFrames;
        }

        int ignoredCount = 0;
        for (int i = 0; i < maxIndex; i++) {
            String string = stepArray[i].toString();
            if (i == 0 || !isIgnoredStackTraceLine(string)) {
                ThrowableProxyUtil.indent(sb, indent);
                sb.append(string);
                if (ignoredCount > 0) {
                    sb.append(" [" + ignoredCount + " ignored]");
                }
                sb.append(CoreConstants.LINE_SEPARATOR);
            } else {
                ++ignoredCount;
            }
        }
        if (ignoredCount > 0) {
            ThrowableProxyUtil.indent(sb, indent);
            sb.append("[" + ignoredCount + " ignored]");
            sb.append(CoreConstants.LINE_SEPARATOR);
            // ignoredCount = 0;
        }

        // print suppressed exceptions
        IThrowableProxy[] suppressed = tp.getSuppressed();
        if (suppressed != null && suppressed.length > 0) {
            sb.append(CoreConstants.LINE_SEPARATOR);
            ThrowableProxyUtil.indent(sb, indent);
            sb.append("Suppressed Exceptions: ");
            sb.append(CoreConstants.LINE_SEPARATOR);
            for (IThrowableProxy current : suppressed) {
                printThrowable(sb, current, indent + 1);
            }
        }
    }

    boolean isIgnoredStackTraceLine(String line) {
        for (String ignoredExceptionPackage : ignoredExceptionPackages) {
            if (line.startsWith(ignoredExceptionPackage)) {
                return true;
            }
        }
        return false;
    }
}
