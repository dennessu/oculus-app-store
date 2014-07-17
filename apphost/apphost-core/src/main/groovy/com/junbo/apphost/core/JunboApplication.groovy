package com.junbo.apphost.core

import com.junbo.apphost.core.logging.Banner
import com.junbo.apphost.core.logging.LoggerInitializer
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.BeansException
import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.support.DefaultListableBeanFactory
import org.springframework.beans.factory.support.RootBeanDefinition
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext
import org.springframework.util.StopWatch

/**
 * Created by kg on 4/21/2014.
 */
@CompileStatic
@SuppressWarnings('UnnecessaryGetter')
@SuppressWarnings('LoggingSwallowsStacktrace')
@SuppressWarnings('SystemExit')
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

            stopWatch.stop()
            LoggerInitializer.logStarted(LOGGER, stopWatch)

        } catch (Exception ex) {
            LOGGER.error('Application failed with exception:', ex)

            if (ctx != null) {
                try {
                    ctx.close()
                } catch (Exception innerEx) {
                    LOGGER.error('Failed to close application:', innerEx)
                }
            }

            LoggerInitializer.stop()

            System.exit(-1)
        }
    }

    public static class JunboApplicationContext extends ClassPathXmlApplicationContext {

        boolean isRefreshed = false

        JunboApplicationContext(String[] configLocations, boolean refresh) throws BeansException {
            super(configLocations, refresh)
        }

        @Override
        protected void onClose() {
            LoggerInitializer.logClosed(LOGGER)

            LoggerInitializer.stop()
        }

        @Override
        protected void prepareRefresh() {
            isRefreshed = false
            super.prepareRefresh()
        }

        @Override
        protected void finishRefresh() {
            super.finishRefresh()
            isRefreshed = true
        }

        @Override
        protected DefaultListableBeanFactory createBeanFactory() {
            return new JunboBeanFactory(internalParentBeanFactory)
        }
    }

    static class JunboBeanFactory extends DefaultListableBeanFactory {

        private static final Logger LOGGER = LoggerFactory.getLogger(JunboBeanFactory)

        private Stack<Long> latencyStack = new Stack<>()

        private final Set<String> beanNames = new HashSet<>()

        JunboBeanFactory(BeanFactory parentBeanFactory) {
            super(parentBeanFactory)
            this.allowBeanDefinitionOverriding = false
        }

        @Override
        protected <T> T doGetBean(String name, Class<T> requiredType, Object[] args, boolean typeCheckOnly) throws BeansException {

            if (beanNames.contains(name)) {
                return super.doGetBean(name, requiredType, args, typeCheckOnly)
            }

            beanNames.add(name)

            latencyStack.push(0)

            long start = System.currentTimeMillis()

            T result = super.doGetBean(name, requiredType, args, typeCheckOnly)

            long latency = System.currentTimeMillis() - start

            long netLatency = latency + latencyStack.pop()

            def message = "doGetBean ${latency.toString().padLeft(5)} ${netLatency.toString().padLeft(5)} " +
                    (".." * latencyStack.size()) + "$name"

            if (netLatency < 200) {
                LOGGER.info(message)
            } else {
                LOGGER.warn(message)
            }

            if (!latencyStack.empty()) {
                latencyStack.push(latencyStack.pop() - latency)
            }

            return result
        }
    }
}
