/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.sharding.id.oculus;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Java doc for OculusIdGeneratorSlotImpl.
 */
public class OculusIdGeneratorSlotImpl implements OculusIdGeneratorSlot {

    private final int shardId;
    private final OculusIdSchema idSchema;
    private final OculusGlobalCounter globalCounter;
    private final Lock lock;
    private volatile SlotData currentSlotData;

    public OculusIdGeneratorSlotImpl(int shardId, OculusIdSchema idSchema, OculusGlobalCounter globalCounter) {
        this.shardId = shardId;
        this.idSchema = idSchema;
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
                    return nextId(slotData);
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

    private long nextId(SlotData slotData) {
        long value = 0;
        value = (value << idSchema.getBitsInGlobalCounter()) + slotData.globalCounter;
        value = (value << idSchema.getBitsInLocalCounter()) + slotData.localCounter.intValue();
        value = (value << idSchema.getBitsInShard()) + shardId;
        value = (value << idSchema.getDataCenterId()) + idSchema.getDataCenterId();
        value = (value << idSchema.getBitsInIdVersion()) + idSchema.getIdVersion();
        return value;
    }

    private SlotData newSlotData() {
        int count = globalCounter.getAndIncrease(shardId, idSchema.getIdType())
                & idSchema.getMasksInGlobalCounter();

        return new SlotData(count);
    }

    private static class SlotData {

        private final int globalCounter;
        private final AtomicInteger localCounter;

        private SlotData(int globalCounter) {
            this.globalCounter = globalCounter;
            localCounter = new AtomicInteger();
        }
    }
}
