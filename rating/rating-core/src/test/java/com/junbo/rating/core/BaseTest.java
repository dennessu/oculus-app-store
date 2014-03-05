/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.core;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by lizwu on 2/20/14.
 */
@ContextConfiguration(locations = {"classpath:spring/context-test.xml"})
public abstract class BaseTest extends AbstractTestNGSpringContextTests {
    protected long generateId() {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            //ignore
        }

        return System.currentTimeMillis();
    }

    protected Date generateDate(String myDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = formatter.parse(myDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
}
