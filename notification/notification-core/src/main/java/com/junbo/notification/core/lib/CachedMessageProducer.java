/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.notification.core.lib;

import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import javax.jms.*;
import java.lang.reflect.Method;

/**
 * JMS MessageProducer decorator that adapts calls to a shared MessageProducer
 * instance underneath, managing QoS settings locally within the decorator.
 *
 * @author Juergen Hoeller
 * @since 2.5.3
 */
class CachedMessageProducer implements MessageProducer, QueueSender, TopicPublisher {
    private static final Method setDeliveryDelayMethod =
            ClassUtils.getMethodIfAvailable(MessageProducer.class, "setDeliveryDelay", long.class);

    private static final Method getDeliveryDelayMethod =
            ClassUtils.getMethodIfAvailable(MessageProducer.class, "getDeliveryDelay");

    private final MessageProducer target;

    private Boolean originalDisableMessageID;

    private Boolean originalDisableMessageTimestamp;

    private Long originalDeliveryDelay;

    private int deliveryMode;

    private int priority;

    private long timeToLive;

    public CachedMessageProducer(MessageProducer target) throws JMSException {
        this.target = target;
        this.deliveryMode = target.getDeliveryMode();
        this.priority = target.getPriority();
        this.timeToLive = target.getTimeToLive();
    }


    @Override
    public void setDisableMessageID(boolean disableMessageID) throws JMSException {
        if (this.originalDisableMessageID == null) {
            this.originalDisableMessageID = this.target.getDisableMessageID();
        }
        this.target.setDisableMessageID(disableMessageID);
    }

    @Override
    public boolean getDisableMessageID() throws JMSException {
        return this.target.getDisableMessageID();
    }

    @Override
    public void setDisableMessageTimestamp(boolean disableMessageTimestamp) throws JMSException {
        if (this.originalDisableMessageTimestamp == null) {
            this.originalDisableMessageTimestamp = this.target.getDisableMessageTimestamp();
        }
        this.target.setDisableMessageTimestamp(disableMessageTimestamp);
    }

    @Override
    public boolean getDisableMessageTimestamp() throws JMSException {
        return this.target.getDisableMessageTimestamp();
    }

    public void setDeliveryDelay(long deliveryDelay) {
        if (this.originalDeliveryDelay == null) {
            this.originalDeliveryDelay = (Long) ReflectionUtils.invokeMethod(getDeliveryDelayMethod, this.target);
        }
        ReflectionUtils.invokeMethod(setDeliveryDelayMethod, this.target, deliveryDelay);
    }

    public long getDeliveryDelay() {
        return (Long) ReflectionUtils.invokeMethod(getDeliveryDelayMethod, this.target);
    }

    @Override
    public void setDeliveryMode(int deliveryMode) {
        this.deliveryMode = deliveryMode;
    }

    @Override
    public int getDeliveryMode() {
        return this.deliveryMode;
    }

    @Override
    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public int getPriority() {
        return this.priority;
    }

    @Override
    public void setTimeToLive(long timeToLive) {
        this.timeToLive = timeToLive;
    }

    @Override
    public long getTimeToLive() {
        return this.timeToLive;
    }

    @Override
    public Destination getDestination() throws JMSException {
        return this.target.getDestination();
    }

    @Override
    public Queue getQueue() throws JMSException {
        return (Queue) this.target.getDestination();
    }

    @Override
    public Topic getTopic() throws JMSException {
        return (Topic) this.target.getDestination();
    }

    @Override
    public void send(Message message) throws JMSException {
        this.target.send(message, this.deliveryMode, this.priority, this.timeToLive);
    }

    @Override
    public void send(Message message, int deliveryMode, int priority, long timeToLive) throws JMSException {
        this.target.send(message, deliveryMode, priority, timeToLive);
    }

    @Override
    public void send(Destination destination, Message message) throws JMSException {
        this.target.send(destination, message, this.deliveryMode, this.priority, this.timeToLive);
    }

    @Override
    public void send(Destination destination, Message message, int deliveryMode, int priority, long timeToLive) throws JMSException {
        this.target.send(destination, message, deliveryMode, priority, timeToLive);
    }

    @Override
    public void send(Queue queue, Message message) throws JMSException {
        this.target.send(queue, message, this.deliveryMode, this.priority, this.timeToLive);
    }

    @Override
    public void send(Queue queue, Message message, int deliveryMode, int priority, long timeToLive) throws JMSException {
        this.target.send(queue, message, deliveryMode, priority, timeToLive);
    }

    @Override
    public void publish(Message message) throws JMSException {
        this.target.send(message, this.deliveryMode, this.priority, this.timeToLive);
    }

    @Override
    public void publish(Message message, int deliveryMode, int priority, long timeToLive) throws JMSException {
        this.target.send(message, deliveryMode, priority, timeToLive);
    }

    @Override
    public void publish(Topic topic, Message message) throws JMSException {
        this.target.send(topic, message, this.deliveryMode, this.priority, this.timeToLive);
    }

    @Override
    public void publish(Topic topic, Message message, int deliveryMode, int priority, long timeToLive) throws JMSException {
        this.target.send(topic, message, deliveryMode, priority, timeToLive);
    }

    //remove jms api 2.0 support
    public void send(Message message, CompletionListener completionListener) throws JMSException{
        throw new JMSException("CompletionListener not supported");
    }

    public void send(Message message, int deliveryMode, int priority, long timeToLive, CompletionListener completionListener)
            throws JMSException{
        throw new JMSException("CompletionListener not supported");
    }

    public void send(Destination destination, Message message, CompletionListener completionListener) throws JMSException{
        throw new JMSException("CompletionListener not supported");
    }

    public void send(Destination destination, Message message, int deliveryMode, int priority, long timeToLive,
                     CompletionListener completionListener) throws JMSException{
        throw new JMSException("CompletionListener not supported");
    }

    @Override
    public void close() throws JMSException {
        // It's a cached MessageProducer... reset properties only.
        if (this.originalDisableMessageID != null) {
            this.target.setDisableMessageID(this.originalDisableMessageID);
            this.originalDisableMessageID = null;
        }
        if (this.originalDisableMessageTimestamp != null) {
            this.target.setDisableMessageTimestamp(this.originalDisableMessageTimestamp);
            this.originalDisableMessageTimestamp = null;
        }
        if (this.originalDeliveryDelay != null) {
            ReflectionUtils.invokeMethod(setDeliveryDelayMethod, this.target, this.originalDeliveryDelay);
            this.originalDeliveryDelay = null;
        }
    }

    @Override
    public String toString() {
        return "Cached JMS MessageProducer: " + this.target;
    }
}
