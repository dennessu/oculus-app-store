/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.sharding.test;

import com.junbo.sharding.IdGenerator;
import com.junbo.sharding.IdSchema;
import com.junbo.sharding.impl.*;
import net.spy.memcached.MemcachedClient;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.log4testng.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Date;

/**
 * Java doc for IdGeneratorTest.
 */
public class IdGeneratorTest {
    private static Logger logger = Logger.getLogger(IdGeneratorTest.class);

    private IdGenerator idGenerator;

    private MemcachedClient client;

    private IdSchema idSchema;

    private final class IdGenerateTask implements Runnable {
        private long itemsToGenerate;

        private IdGenerateTask(long itemsToGenerate) {
            this.itemsToGenerate = itemsToGenerate;
        }

        @Override
        public void run() {
            for (int i = 0; i < itemsToGenerate; ++i) {
                long objectId = idGenerator.nextId(123123);

                System.out.println(idSchema.parseObjectId(objectId));

                /*
                String key = Long.toString(objectId.getValue());

                if(client.get(key) != null) {
                    System.err.println("Duplicate found: " + objectId);
                } else {
                    client.add(key, 3600 * 24, 1);
                }
                */
            }
        }
    }

    @BeforeSuite
    public void setup() throws IOException {

        idSchema = new IdSchema(1, 31, (int) (new Date(2014, 01, 01).getTime() / 1000), 12, 10, 1000);

        TimeGenerator timeGenerator = new TimeGeneratorImpl();

        client = new MemcachedClient(new InetSocketAddress(InetAddress.getByName("10.0.0.100"), 11211));

        GlobalCounter globalCounter = new GlobalCounterMemcachedImpl(client, 3600);

        idGenerator = new IdGeneratorImpl(idSchema, timeGenerator, globalCounter);
    }

    @AfterSuite
    public void teardown() {
    }

    //@Test
    public void testGenerateId() {
        final int kThreadCount = 10;
        final int kRandomsToGenerate = 10000;

        Thread [] threads = new Thread[kThreadCount];

        for (int i = 0; i < kThreadCount; ++i) {
            threads[i] = new Thread(new IdGenerateTask(kRandomsToGenerate));
        }

        Date timeStart = new Date();
        for (int i = 0; i < kThreadCount; ++i) {
            threads[i].start();
        }

        for (int i = 0; i < kThreadCount; ++i) {
            try {
                threads[i].join();
            }
            catch (Exception ex) {
                throw new RuntimeException("Thread Join failed.", ex);
            }
        }
        Date timeEnd = new Date();

        System.out.println("Elapsed time in ms: " + (timeEnd.getTime() - timeStart.getTime()));
    }
}
