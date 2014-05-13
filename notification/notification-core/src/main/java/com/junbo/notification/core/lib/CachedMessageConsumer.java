/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.notification.core.lib;

import javax.jms.*;

/**
 * JMS MessageConsumer decorator that adapts all calls
 * to a shared MessageConsumer instance underneath.
 *
 * @author Juergen Hoeller
 * @since 2.5.6
 */
class CachedMessageConsumer implements MessageConsumer, QueueReceiver, TopicSubscriber {

    protected final MessageConsumer target;


    public CachedMessageConsumer(MessageConsumer target) {
        this.target = target;
    }


    @Override
    public String getMessageSelector() throws JMSException {
        return this.target.getMessageSelector();
    }

    @Override
    public Queue getQueue() throws JMSException {
        return (this.target instanceof QueueReceiver ? ((QueueReceiver) this.target).getQueue() : null);
    }

    @Override
    public Topic getTopic() throws JMSException {
        return (this.target instanceof TopicSubscriber ? ((TopicSubscriber) this.target).getTopic() : null);
    }

    @Override
    public boolean getNoLocal() throws JMSException {
        return (this.target instanceof TopicSubscriber && ((TopicSubscriber) this.target).getNoLocal());
    }

    @Override
    public MessageListener getMessageListener() throws JMSException {
        return this.target.getMessageListener();
    }

    @Override
    public void setMessageListener(MessageListener messageListener) throws JMSException {
        this.target.setMessageListener(messageListener);
    }

    @Override
    public Message receive() throws JMSException {
        return this.target.receive();
    }

    @Override
    public Message receive(long timeout) throws JMSException {
        return this.target.receive(timeout);
    }

    @Override
    public Message receiveNoWait() throws JMSException {
        return this.target.receiveNoWait();
    }

    @Override
    public void close() throws JMSException {
        // It's a cached MessageConsumer...
    }


    @Override
    public String toString() {
        return "Cached JMS MessageConsumer: " + this.target;
    }

}

