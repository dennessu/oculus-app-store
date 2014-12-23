/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.sharding.id.impl;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Java doc for Time based id generator, it should have no concept of dataCenter.
 */
public class IdGeneratorSlotImpl implements IdGeneratorSlot {

    private final int shardId;

    private final IdSchema idSchema;

    private final TimeGenerator timeGenerator;
    private final GlobalCounter globalCounter;

    private final Lock lock;

    private volatile SlotData currentSlotData;

    public IdGeneratorSlotImpl(int shardId, IdSchema idSchema,
                               TimeGenerator timeGenerator, GlobalCounter globalCounter) {

        this.shardId = shardId;
        this.idSchema = idSchema;

        this.timeGenerator = timeGenerator;
        this.globalCounter = globalCounter;

        this.lock = new ReentrantLock();
    }

    @Override
    public long nextId() {

        if (currentSlotData == null) {
            lock.lock();

            try {

                if (currentSlotData == null) {
                    currentSlotData = newSlotData();
                }

            }
            finally {
                lock.unlock();
            }
        }

        while (true) {

            SlotData slotData = currentSlotData;
            assert slotData != null : "slotData is null";

            int current = slotData.localCounter.get();

            if (current <= idSchema.getMasksInLocalCounter()) {
                if (slotData.localCounter.compareAndSet(current, current + 1)) {
                    return nextId(slotData, current);
                }
            }
            else {
                lock.lock();

                try {

                    if (slotData == currentSlotData) {
                        currentSlotData = newSlotData();
                    }

                }
                finally {
                    lock.unlock();
                }
            }
        }
    }

    private long nextId(SlotData slotData, int localCounter) {
        long value = slotData.timeSec;
        value = (value << idSchema.getBitsInGlobalCounter()) + slotData.globalCounter;
        value = (value << idSchema.getBitsInLocalCounter()) + localCounter;
        value = value * idSchema.getNumberOfShards() + shardId;
        return value;
    }

    private SlotData newSlotData() {

        int currentTimeSec = timeGenerator.currentTimeSec(idSchema.getTimeSecOffset()) & idSchema.getMasksInTime();

        int count = globalCounter.getAndIncrease(shardId, currentTimeSec)
                & idSchema.getMasksInGlobalCounter();

        return new SlotData(currentTimeSec, count);
    }

    private static class SlotData {

        private final int timeSec;
        private final int globalCounter;

        private final AtomicInteger localCounter;

        private SlotData(int timeSec, int globalCounter) {
            this.timeSec = timeSec;
            this.globalCounter = globalCounter;

            localCounter = new AtomicInteger();
        }
    }
}
