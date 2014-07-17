package com.junbo.catalog.rest;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;

@ContextConfiguration(locations = {"classpath:spring/context-test.xml"})
public abstract class BaseTest extends AbstractTestNGSpringContextTests {
    @BeforeTest
    @SuppressWarnings("deprecation")
    public void setup() {

    }

    @AfterTest
    @SuppressWarnings("deprecation")
    public void cleanup() {

    }
}
