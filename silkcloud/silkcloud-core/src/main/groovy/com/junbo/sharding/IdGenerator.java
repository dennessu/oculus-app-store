/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.sharding;

/**
 *  Distributed ID Generator to generate unique IDs for the system.
 *  The ID generated is a positive 64-bit integer. The ID has the following property:
 *  <ul>
 *      <li>The ID generated is almost sequential.</li>
 *      <li>The ID generated is globally unique in the system.</li>
 *  </ul>
 */
public interface IdGenerator {

    /**
     * Generate a new ID, the shardId is random distributed.
     * @return The new unique ID on one shard.
     */
    long nextId();

    /**
     * Generate a new ID following the shard of the existing ID.
     * @param id The source(seed) ID for shard information.
     * @return The new unique ID on the same shard as the source ID.
     */
    long nextId(long id);

    /*
     * Generate a new ID given the shard ID
     * @param dataCenterId The expected dataCenterId.
     * @param shardId The expected shardId.
     * @return The new unqiue ID on the expected shard.
     */
    long nextIdByDCIdAndShardId(int dataCenterId, int shardId);
}
