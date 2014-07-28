/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.common.util;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.serializers.CollectionSerializer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * CloneUtils.
 */
public final class CloneUtils {
    private static ThreadLocal<Kryo> kryo = new ThreadLocal<Kryo>();

    private CloneUtils() {
    }

    public static <T> T clone(T source) {
        if (source == null)
            return null;

        return kryoClone(source);
    }

    public static <T> T kryoClone(T source) {
        if (source == null) {
            return null;
        }

        T result = source;
        try {
            result = getOrCreateLocalKryo().copy(source);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    private static Kryo getOrCreateLocalKryo() {
        Kryo localKryo = kryo.get();
        if (localKryo == null) {
            localKryo = new Kryo();
            localKryo.register(Arrays.asList().getClass(), new ArraysAsListSerializer());

            kryo.set(localKryo);
        }

        return localKryo;
    }

    /**
     * ArraysAsListSerializer.
     */
    public static class ArraysAsListSerializer extends CollectionSerializer {
        @Override
        public Collection copy(Kryo kryo, Collection original) {
            return super.copy(kryo, new ArrayList(original));
        }
    }
}
