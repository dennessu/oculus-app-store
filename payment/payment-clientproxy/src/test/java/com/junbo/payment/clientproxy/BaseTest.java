package com.junbo.payment.clientproxy;

import com.junbo.sharding.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;

import java.util.UUID;

@ContextConfiguration(locations = {"classpath:spring/context-test.xml"})
public abstract class BaseTest extends AbstractTestNGSpringContextTests {

    @Autowired
    @Qualifier("oculus48IdGenerator")
    private IdGenerator idGenerator;
    protected long getLongId() {
        //shard id
        return idGenerator.nextId();
    }

    protected UUID generateUUID() {
        return UUID.randomUUID();
    }

    @BeforeTest
    @SuppressWarnings("deprecation")
    public void setup() {
    }

    @AfterTest
    @SuppressWarnings("deprecation")
    public void cleanup() {
    }

}
