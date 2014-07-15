package com.junbo.catalog.rest;

import com.junbo.langur.core.promise.ExecutorContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;

@ContextConfiguration(locations = {"classpath:spring/context-test.xml"})
public abstract class BaseTest extends AbstractTestNGSpringContextTests {
    @BeforeTest
    @SuppressWarnings("deprecation")
    public void setup() {
        ExecutorContext.setAsyncMode(false);
    }

    @AfterTest
    @SuppressWarnings("deprecation")
    public void cleanup() {
        ExecutorContext.resetAsyncMode();
    }
}
