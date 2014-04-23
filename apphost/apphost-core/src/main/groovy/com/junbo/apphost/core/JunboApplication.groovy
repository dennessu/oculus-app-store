package com.junbo.apphost.core

import com.junbo.apphost.core.logging.Banner
import com.junbo.apphost.core.logging.LoggerInitializer
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.BeansException
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext
import org.springframework.util.StopWatch

/**
 * Created by kg on 4/21/2014.
 */
@CompileStatic
@SuppressWarnings('UnnecessaryGetter')
@SuppressWarnings('LoggingSwallowsStacktrace')
class JunboApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(JunboApplication)

    static void main(String[] args) {

        StopWatch stopWatch = new StopWatch()
        stopWatch.start()

        Banner.write(System.out)

        LoggerInitializer.logStarting(LOGGER)

        ConfigurableApplicationContext ctx = null

        try {
            ctx = new JunboApplicationContext(['classpath*:/spring/**/*.xml'] as String[], false)

            ctx.registerShutdownHook()
            ctx.refresh()

            LOGGER.info("""Application started with classpath:
${getClasspath().join(System.lineSeparator())}""")

            stopWatch.stop()
            LoggerInitializer.logStarted(LOGGER, stopWatch)

        } catch (Throwable ex) {
            if (ctx != null) {
                ctx.close()
            }

            LOGGER.error('Application failed with exception:', ex)

            LOGGER.error("""Application failed with classpath:
${getClasspath().join(System.lineSeparator())}""")

            LoggerInitializer.stop()

            throw ex
        }
    }

    private static List<String> getClasspath() {
        ClassLoader classLoader = Thread.currentThread().contextClassLoader

        if (classLoader instanceof URLClassLoader) {
            return ((URLClassLoader) classLoader).URLs.collect { URL it -> it.toString() }
        }

        return ['unknown']
    }

    private static class JunboApplicationContext extends ClassPathXmlApplicationContext {

        JunboApplicationContext(String[] configLocations, boolean refresh) throws BeansException {
            super(configLocations, refresh)
        }

        @Override
        protected void onClose() {
            LoggerInitializer.logClosed(LOGGER)

            LoggerInitializer.stop()
        }
    }
}
