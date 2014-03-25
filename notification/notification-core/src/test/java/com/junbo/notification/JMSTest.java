package com.junbo.notification;

import com.junbo.notification.queue.TestProducer;
import com.junbo.notification.retry.TransactionalListener;
import com.junbo.notification.topic.TestPublisher;
import org.apache.activemq.broker.BrokerService;
import org.junit.BeforeClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import java.util.concurrent.atomic.AtomicInteger;

@ContextConfiguration(locations = {"classpath:spring/context-test-*.xml"})
public class JMSTest extends AbstractTestNGSpringContextTests {
    @Autowired
    private BrokerService broker;

    @Autowired
    @Qualifier("testProducer")
    private TestProducer producer;

    @Autowired
    @Qualifier("testProducer2")
    private TestProducer producer2;

    @Autowired
    private TestPublisher publisher;

    @BeforeClass
    public void setUp() throws Exception {
        broker.setSystemExitOnShutdown(true);
    }

    @AfterClass
    public void tearDown() throws Exception {
        broker.stop();
    }

    @Test
    public void testQueue() {
        producer.send("hello baby!");
    }

    @Test
    public void testTopic() {
        publisher.send("come on baby!");
    }

    @Test
    public void testTransactionalListener() throws Exception {
        TransactionalListener.FAILED_COUNT = new AtomicInteger(0);
        producer2.send("come on failure!");

        Thread.sleep(5000);
    }
}
