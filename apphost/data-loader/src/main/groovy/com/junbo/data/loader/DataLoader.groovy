/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.data.loader

import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.util.ContextInitializer
import com.junbo.apphost.core.JunboApplication
import com.junbo.authorization.AuthorizeContext
import com.junbo.authorization.AuthorizeServiceImpl
import com.junbo.configuration.ConfigService
import com.junbo.configuration.ConfigServiceManager
import com.junbo.data.handler.DataHandler
import groovy.transform.CompileStatic
import org.apache.commons.io.IOUtils
import org.glassfish.grizzly.threadpool.JunboThreadPool
import org.slf4j.ILoggerFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.impl.StaticLoggerBinder
import org.springframework.beans.factory.annotation.Required
import org.springframework.context.ApplicationContext
import org.springframework.core.io.Resource
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.util.CollectionUtils
import org.springframework.util.ResourceUtils
import org.springframework.util.StringUtils
import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J

import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService

/**
 * DataLoader.
 */
@CompileStatic
class DataLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataLoader)
    private static Map<String, DataHandler> handlers
    private static List<String> dataList
    private static Set<String> serialDataList
    private static String env = "_default"
    static PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(this.class.classLoader)

    static void main(String[] args) {
        configLog()

        LOGGER.info("loading spring context start")
        ApplicationContext applicationContext = new JunboApplication.JunboApplicationContext(
                ["classpath*:/spring/*-context.xml",
                        "classpath*:/spring/validators.xml",
                        "classpath*:/spring/transaction.xml",
                        "classpath*:/spring/flow/*.xml"] as String[], true)
        applicationContext.getBean("dataLoader", DataLoader)
        LOGGER.info("loading spring context end")

        ConfigService configService = ConfigServiceManager.instance()
        String configEnv = configService.getConfigContext().getEnvironment()
        if (!StringUtils.isEmpty(configEnv)) {
            env = configEnv
        }
        LOGGER.info("current environment is $env")

        AuthorizeServiceImpl authorizeService = applicationContext.getBean(AuthorizeServiceImpl)
        authorizeService.setDisabled(true)
        AuthorizeContext.setAuthorizeDisabled(true)

        JunboThreadPool junboThreadPool = applicationContext.getBean("junboThreadPool", JunboThreadPool)
        // in order to eagerly initialize the thread pool
        if (junboThreadPool == null) {
            throw new IllegalStateException("junboThreadPool is null")
        }

        if (args.length == 0 || args[0].equalsIgnoreCase("all")) {
            LOGGER.info("loading all data")
            try {
                load(dataList, junboThreadPool)

                LOGGER.info("loading data end")
            } finally {
                exit()
            }
        } else if (args.length == 1 && args[0].equalsIgnoreCase("masterkey")) {
            try {
                DataHandler handler = handlers["masterkey"];
                handler.handle(null)
            } catch (Exception e) {
                LOGGER.error("Error occured while generating masterkey", e)
            } finally {
                exit()
            }
        } else {
            List<String> list = CollectionUtils.arrayToList(args)

            checkData(list)

            try {
                load(list, junboThreadPool)
                LOGGER.info("loading data end")
            } finally {
                exit()
            }
        }
    }

    static void checkData(List<String> list) {
        list.each { data ->
            if (!dataList.contains(data)) {
                LOGGER.error("$data not found in directory")
                exit()
            }
        }
    }

    static void load(List<String> dataList, ExecutorService pool) {

        for (String data : dataList) {
            Resource[] resources
            Boolean isSerial = serialDataList.contains(data)

            try {
                resources = resolver.getResources("data/$env/$data/*.data")
            } catch (FileNotFoundException e) {
                try {
                    resources = resolver.getResources("data/_default/$data/*.data")
                } catch (Exception ex) {
                    continue
                }
            }

            if (resources.length == 0) {
                continue
            }

            CountDownLatch latch = new CountDownLatch(resources.length)

            try {
                DataHandler handler = handlers[data]
                resources = handler.resolveDependencies(resources)

                if (isSerial) {
                    resources.each { Resource resource ->
                        if (handler != null) {
                            LOGGER.info("handling resource: " + data + " " + resource.filename)
                            String content = IOUtils.toString(resource.URI, "UTF-8")
                            handler.handle(content)
                        } else {
                            LOGGER.error("no handler for $data")
                            exit()
                        }
                    }
                } else {
                    resources.each { Resource resource ->
                        pool.submit(new Runnable() {
                            @Override
                            void run() {
                                if (handler != null) {
                                    LOGGER.info("handling resource: " + data + " " + resource.filename)
                                    String content = IOUtils.toString(resource.URI, "UTF-8")
                                    handler.handle(content)
                                } else {
                                    LOGGER.error("no handler for $data")
                                    exit()
                                }
                                latch.countDown()
                            }
                        })
                    }
                }
            } catch (Exception e) {
                LOGGER.error("Error ocuured while loading $data", e)
                exit()
            }

            if (!isSerial) {
                latch.await()
            }
        }
    }

    static void configLog() {
        System.setProperty('net.spy.log.LoggerImpl', 'net.spy.memcached.compat.log.SLF4JLogger')
        System.setProperty('org.jboss.logging.provider', 'slf4j')
        ILoggerFactory factory = StaticLoggerBinder.singleton.loggerFactory
        LoggerContext context = (LoggerContext) factory
        context.stop()
        String location = 'classpath:logging/logback-dataloader.xml'

        try {
            URL url = ResourceUtils.getURL(location)
            new ContextInitializer(context).configureByResource(url)
        } catch (Exception ex) {
            throw new IllegalStateException("Could not initialize logging from $location", ex)
        }

        SysOutOverSLF4J.sendSystemOutAndErrToSLF4J()
    }

    @Required
    void setHandlers(Map<String, DataHandler> handlers) {
        this.handlers = handlers
    }

    @Required
    void setDataList(List<String> dataList) {
        this.dataList = dataList
    }

    @Required
    void setSerialDataList(Set<String> serialDataList) {
        DataLoader.serialDataList = serialDataList
    }

    private static void exit() {
        System.exit(0)
    }
}
