/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.notification.core.lib;

import org.springframework.jms.connection.SessionProxy;
import org.springframework.jms.connection.SingleConnectionFactory;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;

import javax.jms.*;
import javax.jms.IllegalStateException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;

/**
 * remove support for JMS API 2.0.
 */
public class CachingConnectionFactory extends SingleConnectionFactory {

    private static final Method createSharedDurableConsumerMethod = ClassUtils.getMethodIfAvailable(
            Session.class, "createSharedDurableConsumer", Topic.class, String.class, String.class);


    private int sessionCacheSize = 1;

    private boolean cacheProducers = true;

    private boolean cacheConsumers = true;

    private volatile boolean active = true;

    private final Map<Integer, LinkedList<Session>> cachedSessions =
            new HashMap<Integer, LinkedList<Session>>();


    /**
     * Create a new CachingConnectionFactory for bean-style usage.
     *
     * @see #setTargetConnectionFactory
     */
    public CachingConnectionFactory() {
        super();
        setReconnectOnException(true);
    }

    /**
     * Create a new CachingConnectionFactory for the given target
     * ConnectionFactory.
     *
     * @param targetConnectionFactory the target ConnectionFactory
     */
    public CachingConnectionFactory(ConnectionFactory targetConnectionFactory) {
        super(targetConnectionFactory);
        setReconnectOnException(true);
    }


    /**
     * Specify the desired size for the JMS Session cache (per JMS Session type).
     * <p>This cache size is the maximum limit for the number of cached Sessions
     * per session acknowledgement type (auto, client, dups_ok, transacted).
     * As a consequence, the actual number of cached Sessions may be up to
     * four times as high as the specified value - in the unlikely case
     * of mixing and matching different acknowledgement types.
     * <p>Default is 1: caching a single Session, (re-)creating further ones on
     * demand. Specify a number like 10 if you'd like to raise the number of cached
     * Sessions; that said, 1 may be sufficient for low-concurrency scenarios.
     *
     * @see #setCacheProducers
     */
    public void setSessionCacheSize(int sessionCacheSize) {
        Assert.isTrue(sessionCacheSize >= 1, "Session cache size must be 1 or higher");
        this.sessionCacheSize = sessionCacheSize;
    }

    /**
     * Return the desired size for the JMS Session cache (per JMS Session type).
     */
    public int getSessionCacheSize() {
        return this.sessionCacheSize;
    }

    /**
     * Specify whether to cache JMS MessageProducers per JMS Session instance
     * (more specifically: one MessageProducer per Destination and Session).
     * <p>Default is "true". Switch this to "false" in order to always
     * recreate MessageProducers on demand.
     */
    public void setCacheProducers(boolean cacheProducers) {
        this.cacheProducers = cacheProducers;
    }

    /**
     * Return whether to cache JMS MessageProducers per JMS Session instance.
     */
    public boolean isCacheProducers() {
        return this.cacheProducers;
    }

    /**
     * Specify whether to cache JMS MessageConsumers per JMS Session instance
     * (more specifically: one MessageConsumer per Destination, selector String
     * and Session). Note that durable subscribers will only be cached until
     * logical closing of the Session handle.
     * <p>Default is "true". Switch this to "false" in order to always
     * recreate MessageConsumers on demand.
     */
    public void setCacheConsumers(boolean cacheConsumers) {
        this.cacheConsumers = cacheConsumers;
    }

    /**
     * Return whether to cache JMS MessageConsumers per JMS Session instance.
     */
    public boolean isCacheConsumers() {
        return this.cacheConsumers;
    }


    /**
     * Resets the Session cache as well.
     */
    @Override
    public void resetConnection() {
        this.active = false;
        synchronized (this.cachedSessions) {
            for (LinkedList<Session> sessionList : this.cachedSessions.values()) {
                synchronized (sessionList) {
                    for (Session session : sessionList) {
                        try {
                            session.close();
                        } catch (Throwable ex) {
                            logger.trace("Could not close cached JMS Session", ex);
                        }
                    }
                }
            }
            this.cachedSessions.clear();
        }
        this.active = true;

        // Now proceed with actual closing of the shared Connection...
        super.resetConnection();
    }

    /**
     * Checks for a cached Session for the given mode.
     */
    @Override
    protected Session getSession(Connection con, Integer mode) throws JMSException {
        LinkedList<Session> sessionList;
        synchronized (this.cachedSessions) {
            sessionList = this.cachedSessions.get(mode);
            if (sessionList == null) {
                sessionList = new LinkedList<Session>();
                this.cachedSessions.put(mode, sessionList);
            }
        }
        Session session = null;
        synchronized (sessionList) {
            if (!sessionList.isEmpty()) {
                session = sessionList.removeFirst();
            }
        }
        if (session != null) {
            if (logger.isTraceEnabled()) {
                logger.trace("Found cached JMS Session for mode " + mode + ": " +
                        (session instanceof SessionProxy ? ((SessionProxy) session).getTargetSession() : session));
            }
        } else {
            Session targetSession = createSession(con, mode);
            if (logger.isDebugEnabled()) {
                logger.debug("Creating cached JMS Session for mode " + mode + ": " + targetSession);
            }
            session = getCachedSessionProxy(targetSession, sessionList);
        }
        return session;
    }

    /**
     * Wrap the given Session with a proxy that delegates every method call to it
     * but adapts close calls. This is useful for allowing application code to
     * handle a special framework Session just like an ordinary Session.
     *
     * @param target      the original Session to wrap
     * @param sessionList the List of cached Sessions that the given Session belongs to
     * @return the wrapped Session
     */
    protected Session getCachedSessionProxy(Session target, LinkedList<Session> sessionList) {
        List<Class<?>> classes = new ArrayList<Class<?>>(3);
        classes.add(SessionProxy.class);
        if (target instanceof QueueSession) {
            classes.add(QueueSession.class);
        }
        if (target instanceof TopicSession) {
            classes.add(TopicSession.class);
        }
        return (Session) Proxy.newProxyInstance(
                SessionProxy.class.getClassLoader(),
                classes.toArray(new Class<?>[classes.size()]),
                new CachedSessionInvocationHandler(target, sessionList));
    }


    /**
     * Invocation handler for a cached JMS Session proxy.
     */
    private class CachedSessionInvocationHandler implements InvocationHandler {

        private final Session target;

        private final LinkedList<Session> sessionList;

        private final Map<DestinationCacheKey, MessageProducer> cachedProducers =
                new HashMap<DestinationCacheKey, MessageProducer>();

        private final Map<ConsumerCacheKey, MessageConsumer> cachedConsumers =
                new HashMap<ConsumerCacheKey, MessageConsumer>();

        private boolean transactionOpen = false;

        public CachedSessionInvocationHandler(Session target, LinkedList<Session> sessionList) {
            this.target = target;
            this.sessionList = sessionList;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String methodName = method.getName();
            if (methodName.equals("equals")) {
                // Only consider equal when proxies are identical.
                return (proxy == args[0]);
            } else if (methodName.equals("hashCode")) {
                // Use hashCode of Session proxy.
                return System.identityHashCode(proxy);
            } else if (methodName.equals("toString")) {
                return "Cached JMS Session: " + this.target;
            } else if (methodName.equals("close")) {
                // Handle close method: don't pass the call on.
                if (active) {
                    synchronized (this.sessionList) {
                        if (this.sessionList.size() < getSessionCacheSize()) {
                            logicalClose((Session) proxy);
                            // Remain open in the session list.
                            return null;
                        }
                    }
                }
                // If we get here, we're supposed to shut down.
                physicalClose();
                return null;
            } else if (methodName.equals("getTargetSession")) {
                // Handle getTargetSession method: return underlying Session.
                return this.target;
            } else if (methodName.equals("commit") || methodName.equals("rollback")) {
                this.transactionOpen = false;
            } else if (methodName.startsWith("create")) {
                this.transactionOpen = true;
                if (isCacheProducers() && (methodName.equals("createProducer") ||
                        methodName.equals("createSender") || methodName.equals("createPublisher"))) {
                    // Destination argument being null is ok for a producer
                    return getCachedProducer((Destination) args[0]);
                } else if (isCacheConsumers()) {
                    // let raw JMS invocation throw an exception if Destination (i.e. args[0]) is null
                    if ((methodName.equals("createConsumer") || methodName.equals("createReceiver") ||
                            methodName.equals("createSubscriber"))) {
                        Destination dest = (Destination) args[0];
                        if (dest != null && !(dest instanceof TemporaryQueue || dest instanceof TemporaryTopic)) {
                            return getCachedConsumer(dest,
                                    (args.length > 1 ? (String) args[1] : null),
                                    (args.length > 2 && (Boolean) args[2]),
                                    null);
                        }
                    } else if (methodName.equals("createDurableConsumer") || methodName.equals("createDurableSubscriber")) {
                        Destination dest = (Destination) args[0];
                        if (dest != null) {
                            return getCachedConsumer(dest,
                                    (args.length > 2 ? (String) args[2] : null),
                                    (args.length > 3 && (Boolean) args[3]),
                                    (String) args[1]);
                        }
                    } else if (methodName.equals("createSharedDurableConsumer")) {
                        Destination dest = (Destination) args[0];
                        if (dest != null) {
                            return getCachedConsumer(dest,
                                    (args.length > 2 ? (String) args[2] : null),
                                    null,
                                    (String) args[1]);
                        }
                    }
                }
            }
            try {
                return method.invoke(this.target, args);
            } catch (InvocationTargetException ex) {
                throw ex.getTargetException();
            }
        }

        private MessageProducer getCachedProducer(Destination dest) throws JMSException {
            DestinationCacheKey cacheKey = (dest != null ? new DestinationCacheKey(dest) : null);
            MessageProducer producer = this.cachedProducers.get(cacheKey);
            if (producer != null) {
                if (logger.isTraceEnabled()) {
                    logger.trace("Found cached JMS MessageProducer for destination [" + dest + "]: " + producer);
                }
            } else {
                producer = this.target.createProducer(dest);
                if (logger.isDebugEnabled()) {
                    logger.debug("Creating cached JMS MessageProducer for destination [" + dest + "]: " + producer);
                }
                this.cachedProducers.put(cacheKey, producer);
            }
            return new CachedMessageProducer(producer);
        }

        private MessageConsumer getCachedConsumer(
                Destination dest, String selector, Boolean noLocal, String subscription) throws JMSException {

            ConsumerCacheKey cacheKey = new ConsumerCacheKey(dest, selector, noLocal, subscription);
            MessageConsumer consumer = this.cachedConsumers.get(cacheKey);
            if (consumer != null) {
                if (logger.isTraceEnabled()) {
                    logger.trace("Found cached JMS MessageConsumer for destination [" + dest + "]: " + consumer);
                }
            } else {
                if (dest instanceof Topic) {
                    if (noLocal == null) {
                        // createSharedDurableConsumer((Topic) dest, subscription, selector);
                        try {
                            consumer = (MessageConsumer) createSharedDurableConsumerMethod.invoke(
                                    this.target, dest, subscription, selector);
                        } catch (InvocationTargetException ex) {
                            if (ex.getTargetException() instanceof JMSException) {
                                throw (JMSException) ex.getTargetException();
                            }
                            ReflectionUtils.handleInvocationTargetException(ex);
                        } catch (IllegalAccessException ex) {
                            throw new IllegalStateException("Could not access JMS 2.0 API method: " + ex.getMessage());
                        }
                    } else {
                        consumer = (subscription != null ?
                                this.target.createDurableSubscriber((Topic) dest, subscription, selector, noLocal) :
                                this.target.createConsumer(dest, selector, noLocal));
                    }
                } else {
                    consumer = this.target.createConsumer(dest, selector);
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("Creating cached JMS MessageConsumer for destination [" + dest + "]: " + consumer);
                }
                this.cachedConsumers.put(cacheKey, consumer);
            }
            return new CachedMessageConsumer(consumer);
        }

        private void logicalClose(Session proxy) throws JMSException {
            // Preserve rollback-on-close semantics.
            if (this.transactionOpen && this.target.getTransacted()) {
                this.transactionOpen = false;
                this.target.rollback();
            }
            // Physically close durable subscribers at time of Session close call.
            for (Iterator<Map.Entry<ConsumerCacheKey, MessageConsumer>> it = this.cachedConsumers.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<ConsumerCacheKey, MessageConsumer> entry = it.next();
                if (entry.getKey().subscription != null) {
                    entry.getValue().close();
                    it.remove();
                }
            }
            // Allow for multiple close calls...
            boolean returned = false;
            synchronized (this.sessionList) {
                if (!this.sessionList.contains(proxy)) {
                    this.sessionList.addLast(proxy);
                    returned = true;
                }
            }
            if (returned && logger.isTraceEnabled()) {
                logger.trace("Returned cached Session: " + this.target);
            }
        }

        private void physicalClose() throws JMSException {
            if (logger.isDebugEnabled()) {
                logger.debug("Closing cached Session: " + this.target);
            }
            // Explicitly close all MessageProducers and MessageConsumers that
            // this Session happens to cache...
            try {
                for (MessageProducer producer : this.cachedProducers.values()) {
                    producer.close();
                }
                for (MessageConsumer consumer : this.cachedConsumers.values()) {
                    consumer.close();
                }
            } finally {
                this.cachedProducers.clear();
                this.cachedConsumers.clear();
                // Now actually close the Session.
                this.target.close();
            }
        }
    }


    /**
     * Simple wrapper class around a Destination reference.
     * Used as the cache key when caching MessageProducer objects.
     */
    private static class DestinationCacheKey {

        private final Destination destination;

        private String destinationString;

        public DestinationCacheKey(Destination destination) {
            Assert.notNull(destination, "Destination must not be null");
            this.destination = destination;
        }

        private String getDestinationString() {
            if (this.destinationString == null) {
                this.destinationString = this.destination.toString();
            }
            return this.destinationString;
        }

        protected boolean destinationEquals(DestinationCacheKey otherKey) {
            return (this.destination.getClass().equals(otherKey.destination.getClass()) &&
                    (this.destination.equals(otherKey.destination) ||
                            getDestinationString().equals(otherKey.getDestinationString())));
        }

        @Override
        public boolean equals(Object other) {
            // Effectively checking object equality as well as toString equality.
            // On WebSphere MQ, Destination objects do not implement equals...
            return (other == this || destinationEquals((DestinationCacheKey) other));
        }

        @Override
        public int hashCode() {
            // Can't use a more specific hashCode since we can't rely on
            // this.destination.hashCode() actually being the same value
            // for equivalent destinations... Thanks a lot, WebSphere MQ!
            return this.destination.getClass().hashCode();
        }
    }


    /**
     * Simple wrapper class around a Destination and other consumer attributes.
     * Used as the cache key when caching MessageConsumer objects.
     */
    private static class ConsumerCacheKey extends DestinationCacheKey {

        private final String selector;

        private final Boolean noLocal;

        private final String subscription;

        public ConsumerCacheKey(Destination destination, String selector, Boolean noLocal, String subscription) {
            super(destination);
            this.selector = selector;
            this.noLocal = noLocal;
            this.subscription = subscription;
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }
            ConsumerCacheKey otherKey = (ConsumerCacheKey) other;
            return (destinationEquals(otherKey) &&
                    ObjectUtils.nullSafeEquals(this.selector, otherKey.selector) &&
                    ObjectUtils.nullSafeEquals(this.noLocal, otherKey.noLocal) &&
                    ObjectUtils.nullSafeEquals(this.subscription, otherKey.subscription));
        }

        @Override
        public int hashCode(){
            return super.hashCode();
        }
    }

}
