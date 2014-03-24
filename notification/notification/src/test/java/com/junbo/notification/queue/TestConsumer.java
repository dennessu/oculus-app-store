package com.junbo.notification.queue;

import junit.framework.Assert;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

public class TestConsumer implements MessageListener {
    @Override
    public void onMessage(Message message) {
        try {
            TextMessage msg = (TextMessage) message;
            Assert.assertNotNull(msg.getText());
        } catch (Exception e) {
            //ignore
        }
    }
}
