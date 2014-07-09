/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.sharding.id.oculus

import groovy.transform.CompileStatic

import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

/**
 * Java doc for OculusIdGeneratorSlotImpl.
 */
@CompileStatic
class OculusIdGeneratorSlotImpl implements OculusIdGeneratorSlot {

    private final int dcId
    private final int shardId
    private final OculusIdSchema idSchema
    private final OculusGlobalCounter globalCounter
    private final Lock lock
    private volatile SlotData currentSlotData

    OculusIdGeneratorSlotImpl(int dcId, int shardId, OculusIdSchema idSchema, OculusGlobalCounter globalCounter) {
        this.dcId = dcId
        this.shardId = shardId
        this.idSchema = idSchema
        this.globalCounter = globalCounter
        this.lock = new ReentrantLock()
    }

    @Override
    long nextId() {
        if (currentSlotData == null) {
            lock.lock()
            try {
                if (currentSlotData == null) {
                    currentSlotData = newSlotData()
                }
            }
            finally {
                lock.unlock()
            }
        }

        while (true) {
            SlotData slotData = currentSlotData
            assert slotData != null : 'slotData is null'

            int current = slotData.localCounter.get()

            if (current <= idSchema.masksInLocalCounter) {
                if (slotData.localCounter.compareAndSet(current, current + 1)) {
                    return generateId(slotData, current)
                }
            }
            else {
                lock.lock()
                try {
                    if (slotData == currentSlotData) {
                        currentSlotData = newSlotData()
                    }
                }
                finally {
                    lock.unlock()
                }
            }
        }
    }

    private long generateId(SlotData slotData, int localCounter) {
        long value = 0
        value = (value << idSchema.bitsInGlobalCounter) + slotData.globalCounter
        value = (value << idSchema.bitsInLocalCounter) + localCounter
        value = (value << idSchema.bitsInShard) + shardId
        value = (value << idSchema.bitsInDataCenterId) + idSchema.dataCenterId
        value = (value << idSchema.bitsInIdVersion) + idSchema.idVersion
        return value
    }

    private SlotData newSlotData() {
        int count = globalCounter.getAndIncrease(dcId, shardId, idSchema.idType) & idSchema.masksInGlobalCounter

        return new SlotData(count)
    }

    private static class SlotData {

        private final int globalCounter
        private final AtomicInteger localCounter

        SlotData(int globalCounter) {
            this.globalCounter = globalCounter
            localCounter = new AtomicInteger()
        }
    }
}
