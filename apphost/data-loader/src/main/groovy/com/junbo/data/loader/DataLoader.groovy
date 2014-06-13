/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.data.loader

import com.junbo.data.handler.DataHandler
import groovy.transform.CompileStatic
import org.apache.commons.io.IOUtils
import org.springframework.beans.factory.annotation.Required
import org.springframework.context.support.ClassPathXmlApplicationContext
import org.springframework.core.io.Resource
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.util.CollectionUtils

/**
 * DataLoader.
 */
@CompileStatic
class DataLoader {
    private static Map<String, DataHandler> handlers
    private static List<String> dataList
    private
    static PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(this.class.classLoader)

    static void main(String[] args) {
        if (args.length == 0) {
            printUsage()
        }
        new ClassPathXmlApplicationContext("classpath*:/spring/*-context.xml")
        if (args[0].equalsIgnoreCase("all")) {
            try {
                load(dataList)
            } finally {
                exit()
            }
        } else {
            List<String> list = CollectionUtils.arrayToList(args)
            list.each { data ->
                if (!dataList.contains(data)) {
                    System.out.println("$data not found")
                    exit()
                }
            }
            try {
                load(list)
            } catch (Exception e){
                System.out.println(e.getStackTrace())
                exit()
            }
            exit()
        }
    }

    static void printUsage() {
        System.out.println('input data to load and input all to load all data')
        exit()
    }

    static void load(List<String> dataList) {
        for (String data : dataList) {
            Resource[] resources = resolver.getResources("data/$data/*.data")
            for (Resource resource : resources) {
                DataHandler handler = handlers[data]
                if (handler != null) {
                    String content = IOUtils.toString(resource.URI)
                    handler.handle(content)
                } else {
                    System.out.println("no handler for $data")
                }
            }
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
