package com.junbo.email.clientproxy

import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests

/**
 * BaseTest Class.
 */
@ContextConfiguration(locations = ['classpath:spring/context-test.xml'])
class BaseTest extends AbstractTestNGSpringContextTests {
}

