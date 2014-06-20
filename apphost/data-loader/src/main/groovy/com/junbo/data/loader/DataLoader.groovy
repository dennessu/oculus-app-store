/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.data.loader

import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.util.ContextInitializer
import com.junbo.data.handler.DataHandler
import groovy.transform.CompileStatic
import org.apache.commons.io.IOUtils
import org.slf4j.ILoggerFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.impl.StaticLoggerBinder
import org.springframework.beans.factory.annotation.Required
import org.springframework.context.support.ClassPathXmlApplicationContext
import org.springframework.core.io.Resource
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.util.CollectionUtils
import org.springframework.util.ResourceUtils

/**
 * DataLoader.
 */
@CompileStatic
class DataLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataLoader)
    private static Map<String, DataHandler> handlers
    private static List<String> dataList
    private
    static PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(this.class.classLoader)

    static void main(String[] args) {
        configLog()
        LOGGER.info("loading spring context start")
        new ClassPathXmlApplicationContext("classpath*:/spring/*-context.xml", "classpath*:/spring/validators.xml", "classpath*:/spring/transaction.xml")
        LOGGER.info("loading spring context end")

        if (args.length == 0 || args[0].equalsIgnoreCase("all")) {
            LOGGER.info("loading all data")
            try {
                load(dataList)
                LOGGER.info("loading data end")
            } finally {
                exit()
            }
        } else {
            List<String> list = CollectionUtils.arrayToList(args)

            checkData(list)

            try {
                load(list)
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

    static void load(List<String> dataList) {
        for (String data : dataList) {
            try {
                Resource[] resources = resolver.getResources("data/$data/*.data")
                DataHandler handler = handlers[data]
                resources = handler.resolveDependencies(resources)

                for (Resource resource : resources) {
                    if (handler != null) {
                        String content = IOUtils.toString(resource.URI)
                        handler.handle(content)
                    } else {
                        LOGGER.error("no handler for $data")
                        exit()
                    }
                }
            } catch (Exception e) {
                LOGGER.error("Error ocuured while loading $data", e)
                exit()
            }
        }
    }

    static void configLog() {
        System.setProperty('net.spy.log.LoggerImpl', 'net.spy.memcached.compat.log.SLF4JLogger')
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
    }

    @Required
    void setHandlers(Map<String, DataHandler> handlers) {
        this.handlers = handlers
    }

    @Required
    void setDataList(List<String> dataList) {
        this.dataList = dataList
    }

    private static void exit() {
        System.exit(0)
    }
}
