package com.junbo.notification;

import com.junbo.notification.queue.TestProducer;
import com.junbo.notification.topic.TestPublisher;
import org.apache.activemq.broker.BrokerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

@ContextConfiguration(locations = {"classpath:spring/context-test-*.xml"})
public class JMSTest extends AbstractTestNGSpringContextTests {
    @Autowired
    private BrokerService broker;

    @Autowired
    private TestProducer producer;

    @Autowired
    private TestPublisher publisher;

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
}
