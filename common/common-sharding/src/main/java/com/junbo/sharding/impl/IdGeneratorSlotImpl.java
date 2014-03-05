/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.sharding.impl;

import com.junbo.sharding.IdSchema;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Java doc for IdGeneratorSlotImpl.
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
                    return nextId(slotData, current, idSchema.getOptionMode());
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

    private long nextId(SlotData slotData, int localCounter, int optionMode) {
        if(optionMode == 1) {
            long value = slotData.timeSec;
            value = (value << idSchema.getBitsInGlobalCounter()) + slotData.globalCounter;
            value = (value << idSchema.getBitsInLocalCounter()) + localCounter;
            value = value * idSchema.getNumberOfShards() + shardId;
            return value;
        } else if (optionMode == 2 || optionMode == 3) {
            long value = 0;
            value = (value << idSchema.getBitsInIdSignificant()) + slotData.idSignificant;
            value = (value << idSchema.getBitsInIdVersion()) + slotData.idVersion;
            value = (value << idSchema.getDataCenterId()) + slotData.dataCenterId;
            value = (value << idSchema.getBitsInShard()) + shardId;
            value = (value << idSchema.getBitsInGlobalCounter()) + slotData.globalCounter;
            value = (value << idSchema.getBitsInLocalCounter()) + slotData.localCounter.intValue();
            return value;
        } else {
            throw new IllegalArgumentException("unsupported optionMode.");
        }
    }

    private SlotData newSlotData() {

        int currentTimeSec = timeGenerator.currentTimeSec(idSchema.getTimeSecOffset()) & idSchema.getMasksInTime();

        int count = globalCounter.getAndIncrease(shardId, currentTimeSec, idSchema.getOptionMode())
                & idSchema.getMasksInGlobalCounter();

        return new SlotData(currentTimeSec, count, idSchema.getDataCenterId(),
                idSchema.getIdVersion(), idSchema.getIdSignificant());
    }

    private static class SlotData {

        private final int timeSec;
        private final int globalCounter;
        private final int dataCenterId;
        private final int idVersion;
        private final int idSignificant;

        private final AtomicInteger localCounter;

        private SlotData(int timeSec, int globalCounter, int dataCenterId, int idVersion, int idSignificant) {
            this.timeSec = timeSec;
            this.globalCounter = globalCounter;
            this.dataCenterId = dataCenterId;
            this.idVersion = idVersion;
            this.idSignificant = idSignificant;

            localCounter = new AtomicInteger();
        }
    }
}
