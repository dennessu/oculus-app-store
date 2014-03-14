package com.junbo.notification;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;

@ContextConfiguration(locations = {"classpath:spring/context-test.xml"})
public abstract class BaseTest extends AbstractTestNGSpringContextTests {
}
