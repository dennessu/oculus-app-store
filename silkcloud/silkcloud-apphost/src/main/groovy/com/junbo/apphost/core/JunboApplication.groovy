package com.junbo.apphost.core

import com.junbo.apphost.core.health.HealthEndpoint
import com.junbo.apphost.core.logging.Banner
import com.junbo.apphost.core.logging.LoggerInitializer
import com.junbo.configuration.ConfigServiceManager
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.BeansException
import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.support.DefaultListableBeanFactory
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader
import org.springframework.context.ApplicationListener
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.event.ContextClosedEvent
import org.springframework.context.support.ClassPathXmlApplicationContext
import org.springframework.util.StopWatch

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CountDownLatch

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

            def running = new CountDownLatch(1)
            ctx.addApplicationListener(new ApplicationListener<ContextClosedEvent>() {
                @Override
                void onApplicationEvent(ContextClosedEvent event) {
                    running.countDown()
                }
            })
            running.await()

            LoggerInitializer.logClosed(LOGGER)

            LoggerInitializer.stop()

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
            setValidating(false)
        }

        @Override
        protected void onClose() {
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

        @Override
        protected void initBeanDefinitionReader(XmlBeanDefinitionReader reader) {
            super.initBeanDefinitionReader(reader)

            reader.documentReaderClass = JunboBeanDefinitionDocumentReader
        }

        @Override
        protected void doClose() {
            int gracePeriod = ConfigServiceManager.instance().getConfigValueAsInt('apphost.gracePeriod', 30);
            LOGGER.info("Wait $gracePeriod seconds before shutdown, put the service offline right now.")
            HealthEndpoint.serviceOnline = false
            Thread.sleep(gracePeriod * 1000)
            super.doClose()
        }
    }

    static class JunboBeanFactory extends DefaultListableBeanFactory {

        private static final Logger LOGGER = LoggerFactory.getLogger(JunboBeanFactory)

        private static final ThreadLocal<Stack<Long>> LATENCY_STACK = new ThreadLocal<Stack<Long>>() {
            @Override
            protected Stack<Long> initialValue() {
                return new Stack<Long>()
            }
        }

        private final Set<String> beanNames = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());

        JunboBeanFactory(BeanFactory parentBeanFactory) {
            super(parentBeanFactory)

            this.allowBeanDefinitionOverriding = false
        }

        @Override
        protected <T> T doGetBean(String name, Class<T> requiredType, Object[] args, boolean typeCheckOnly) throws BeansException {

            if (!beanNames.add(name)) {
                return super.doGetBean(name, requiredType, args, typeCheckOnly)
            }

            def latencyStack = LATENCY_STACK.get()

            latencyStack.push(0)

            long start = System.currentTimeMillis()

            try {
                T result = super.doGetBean(name, requiredType, args, typeCheckOnly)

                return result
            } finally {
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
            }
        }
    }
}
