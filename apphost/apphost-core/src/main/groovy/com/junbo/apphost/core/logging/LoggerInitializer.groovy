package com.junbo.apphost.core.logging

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.util.ContextInitializer
import groovy.transform.CompileStatic
import org.slf4j.ILoggerFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.bridge.SLF4JBridgeHandler
import org.slf4j.impl.StaticLoggerBinder
import org.springframework.util.ResourceUtils
import org.springframework.util.StopWatch
import org.springframework.util.StringUtils

import java.lang.management.ManagementFactory
import java.lang.management.RuntimeMXBean
import java.util.concurrent.Callable

/**
 * Created by kg on 4/21/2014.
 */
@CompileStatic
class LoggerInitializer {

    static final String PID_KEY = 'PID'

    private static Logger LOGGER = LoggerFactory.getLogger(LoggerInitializer)

    static {
        SLF4JBridgeHandler.removeHandlersForRootLogger()
        SLF4JBridgeHandler.install()

        System.setProperty('net.spy.log.LoggerImpl', 'net.spy.memcached.compat.log.SLF4JLogger')
    }

    static {

        if (System.getProperty(PID_KEY) == null) {

            String applicationPid
            try {
                applicationPid = getApplicationPid()
            } catch (IllegalStateException ignored) {
                applicationPid = '????'
            }

            System.setProperty(PID_KEY, applicationPid)
        }


        ILoggerFactory factory = StaticLoggerBinder.singleton.loggerFactory
        LoggerContext context = (LoggerContext) factory

        context.stop()

        String location = 'classpath:logging/logback-apphost.xml'

        try {
            URL url = ResourceUtils.getURL(location)
            new ContextInitializer(context).configureByResource(url)
        } catch (Exception ex) {
            throw new IllegalStateException("Could not initialize logging from $location", ex)
        }
    }

    static void logStarting(Logger logger) {
        logger.info(getStartingMessage())
    }

    static void logStarted(Logger logger, StopWatch stopWatch) {
        logger.info(getStartedMessage(stopWatch))
    }

    static void setLogLevel(String loggerName, String level) {
        ILoggerFactory factory = StaticLoggerBinder.singleton.loggerFactory

        Logger logger = factory.getLogger(StringUtils.isEmpty(loggerName) ? Logger.ROOT_LOGGER_NAME : loggerName)
        ((ch.qos.logback.classic.Logger) logger).setLevel(Level.toLevel(level))
    }

    static void stop() {
        ILoggerFactory factory = StaticLoggerBinder.singleton.loggerFactory
        LoggerContext context = (LoggerContext) factory

        context.stop()
    }

    static private String getStartingMessage() {
        StringBuilder message = new StringBuilder()

        message.append("Starting ");
        message.append(getApplicationName())
        message.append(getVersion())
        message.append(getOn())
        message.append(getPid())
        message.append(getContext())

        return message.toString()
    }

    static private String getStartedMessage(StopWatch stopWatch) {

        StringBuilder message = new StringBuilder()

        message.append("Started ")
        message.append(getApplicationName())
        message.append(" in ");
        message.append(stopWatch.getTotalTimeSeconds());

        double uptime = ManagementFactory.getRuntimeMXBean().getUptime() / 1000.0
        message.append(" seconds (JVM running for " + uptime + ")")

        return message.toString()
    }

    static  private String getApplicationName() {
        return 'Junbo Application Host'
    }

    static  private String getVersion() {
        return getValue(" v", new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return this.class.package.implementationVersion
            }
        }, " vUndefined")
    }

    static  private String getOn() {
        return getValue(" on ", new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return InetAddress.localHost.hostName
            }
        })
    }

    static  private String getPid() {
        return getValue(" with PID ", new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return System.getProperty("PID")
            }
        });
    }

    static  private String getContext() {
        String startedBy = getValue("started by ", new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return System.getProperty("user.name")
            }
        })

        String dir = getValue("in ", new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return System.getProperty("user.dir")
            }
        });

        return " ($startedBy $dir)"
    }

    private static String getValue(String prefix, Callable<Object> call) {
        return getValue(prefix, call, "");
    }

    private static String getValue(String prefix, Callable<Object> call, String defaultValue) {

        try {
            Object value = call.call()
            if (value != null && StringUtils.hasLength(value.toString())) {
                return prefix + value
            }
        }
        catch (Exception ignored) {
            // Swallow and continue
        }

        return defaultValue
    }

    static String getApplicationPid() {

        String pid = null

        try {

            RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean()
            String jvmName = runtimeBean.getName()

            if (StringUtils.isEmpty(jvmName)) {
                LOGGER.warn("Cannot get JVM name")
            }

            if (!jvmName.contains("@")) {
                LOGGER.warn("JVM name doesn't contain process id")
            }

            pid = jvmName.split("@")[0]

        } catch (Throwable e) {
            LOGGER.warn("Cannot get RuntimeMXBean", e)
        }

        if (pid == null) {
            throw new IllegalStateException("Application PID not found")
        }

        return pid
    }

    private LoggerInitializer() {
    }
}
