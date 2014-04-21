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
class JunboApplication {

    private static Logger LOGGER = LoggerFactory.getLogger(JunboApplication)

    static void main(String[] args) {

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Banner.write(System.out)

        LoggerInitializer.logStarting(LOGGER)

        ConfigurableApplicationContext ctx = null

        try {
            ctx = new JunboApplicationContext(['classpath*:/spring/*.xml'] as String[], false)

            ctx.registerShutdownHook()
            ctx.refresh()

            LoggerInitializer.logStarted(LOGGER, stopWatch)

            LOGGER.info("""Application started with classpath:
${getClasspath().join(System.lineSeparator())}""")
        } catch (Exception ex) {
            if (ctx != null) {
                ctx.close()
            }

            LOGGER.error("""Application failed with classpath:
${getClasspath().join(System.lineSeparator())}""")
            LOGGER.error("Application failed with exception:", ex)

            LoggerInitializer.stop()
            throw ex
        }
    }

    private static List<String> getClasspath() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader()

        if (classLoader instanceof URLClassLoader) {
            return ((URLClassLoader) classLoader).getURLs().collect { URL it -> it.toString() }
        }

        return ["unknown"]
    }

    private static class JunboApplicationContext extends ClassPathXmlApplicationContext {

        JunboApplicationContext(String[] configLocations, boolean refresh) throws BeansException {
            super(configLocations, refresh)
        }

        @Override
        protected void onClose() {
            LoggerInitializer.stop()
        }
    }
}