/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.sharding.id.oculus

import com.junbo.common.util.Context
import com.junbo.configuration.topo.DataCenters
import groovy.transform.CompileStatic

import java.security.SecureRandom

/**
 * Distributed ID Generator to generate unique IDs for the system.
 * The ID generated is a positive 64-bit integer. The ID has the following property:
 * <ul>
 * <li>The ID generated is almost sequential.</li>
 * <li>The ID generated is globally unique in the system.</li>
 * </ul>
 * <p/>
 * <pre>{@code
 *  hexValue = timeStamp << 22 | globalCounter << 10 | localCounter
 *  id = hexValue * 1000 + shardId
 *
 *  timeStamp:      The seconds since 2014/1/1. The highest bit is not used to avoid negative values, so it's 31 bits.
 *                  The value can be used until 2082.
 *  globalCounter:  A 12-bit global counter per shard + timeStamp % (2 * secondsToTrack).
 *                  the expiration of the counter is secondsToTrack.
 *  localCounter:   A 10-bit local counter implemented using AtomicInteger.
 *  shardId:        The logical shard ID used for data sharding, ranges from 0 to 999.
 *  }
 *  </pre>
 */
@CompileStatic
class OculusIdGeneratorImpl implements com.junbo.sharding.IdGenerator {

    private final Map<String, OculusIdGeneratorSlot> slotsMap = new HashMap<>()

    private final Random random

    private final OculusIdSchema idSchema

    OculusIdGeneratorImpl(OculusIdSchema idSchema, OculusGlobalCounter globalCounter) {

        this.idSchema = idSchema
        idSchema.getNumberOfShardsMap().entrySet().each { Map.Entry<Integer, Integer> entry ->
            for (int index = 0; index < entry.value; index ++) {
                slotsMap.put([entry.key, index].join(','), new OculusIdGeneratorSlotImpl(entry.key, index, idSchema, globalCounter))
            }
        }

        random = new SecureRandom()
    }

    @Override
    long nextId() {
        int shardId;
        int dcId;

        def topology = Context.get().topology;
        if (topology != null) {
            dcId = topology.currentDCId
            shardId = topology.randomShardId
        } else {
            dcId = DataCenters.instance().currentDataCenterId()
            Integer shardNumber = idSchema.getNumberOfShardsMap().get(dcId)
            if (shardNumber == null) {
                throw new IllegalArgumentException('dcId: ' + dcId + ' isn\'t configured.')
            }
            shardId = random.nextInt(shardNumber)
        }
        return nextIdByDCIdAndShardId(dcId, shardId)
    }

    @Override
    long nextId(long id) {
        OculusObjectId objectId = idSchema.parseObjectId(id)
        int shardId = objectId.shardId
        int dataCenterId = objectId.dataCenterId
        return nextIdByDCIdAndShardId(dataCenterId, shardId)
    }

    /**
     * Generate a new ID given the shard ID.
     * @param dataCenterId The expected dataCenterId
     * @param shardId The expected shardId.
     * @return The new unqiue ID on the expected shard.
     */
    @Override
    long nextIdByDCIdAndShardId(int dataCenterId, int shardId) {
        OculusIdGeneratorSlot slot = slotsMap.get([dataCenterId, shardId].join(','))
        return slot.nextId()
    }
}
