/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.sharding;

import java.util.Map;

/**
 * Created by liangfu on 3/10/14.
 */
public class IdGeneratorFacadeImpl implements IdGeneratorFacade {
    private final Map<Class, IdGenerator> map;
    private final IdGenerator defaultIdGenerator;

    public IdGeneratorFacadeImpl(Map<Class, IdGenerator> map, IdGenerator idGenerator) {
        if(map == null) {
            throw new IllegalArgumentException("map is null.");
        }
        this.map = map;
        this.defaultIdGenerator = idGenerator;
    }

    /**
     * Generate a new ID, the shardId is random distributed.
     * @return The new unique ID on one shard.
     */
    @Override
    public long nextId(Class cls) {
        IdGenerator idGenerator = map.get(cls);
        if(idGenerator == null) {
            idGenerator = defaultIdGenerator;
        }
        return idGenerator.nextId();
    }

    /**
     * Generate a new ID following the shard of the existing ID.
     * @param id The source(seed) ID for shard information.
     * @return The new unique ID on the same shard as the source ID.
     */
    @Override
    public long nextId(Class cls, long id) {
        IdGenerator idGenerator = map.get(cls);
        if(idGenerator == null) {
            idGenerator = defaultIdGenerator;
        }
        return idGenerator.nextId(id);
    }
}
