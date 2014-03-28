package com.junbo.notification;

import com.junbo.notification.queue.TestProducer;
import com.junbo.notification.retry.SessionAwareListener;
import com.junbo.notification.retry.TestSubscriber11;
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
    @Qualifier("testProducer3")
    private TestProducer producer3;

    @Autowired
    @Qualifier("testPublisher")
    private TestPublisher publisher;

    @Autowired
    @Qualifier("testPublisher2")
    private TestPublisher publisher2;

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

    @Test(enabled = false)
    public void testTransactionalListener() throws Exception {
        TransactionalListener.FAILED_COUNT = new AtomicInteger(0);
        producer2.send("come on failure!");

        Thread.sleep(5000);
    }

    @Test(enabled = false)
    public void testSessionAwareListener() throws Exception {
        SessionAwareListener.FAILED_COUNT = new AtomicInteger(0);
        producer3.send("come on session aware failure!");

        Thread.sleep(5000);
    }

    @Test(enabled = false)
    public void testTopicRetry() throws Exception {
        TestSubscriber11.FAILED_COUNT = new AtomicInteger(0);
        publisher2.send("test topic retry!");

        Thread.sleep(5000);
    }
}
