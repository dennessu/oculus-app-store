/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.common.lib;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.serializers.CollectionSerializer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Util to clone object.
 */
public class CloneUtils {
    private static ThreadLocal<Kryo> kryo = new ThreadLocal<Kryo>();

    private CloneUtils() {
    }

    public static <T> T clone(T source) {
        if (source == null) {
            return null;
        }

        T result = source;
        try {
            result = getKryo().copy(source);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    private static Kryo getKryo() {
        Kryo localKryo = kryo.get();
        if (localKryo == null) {
            localKryo = new Kryo();
            localKryo.register(Arrays.asList().getClass(), new ArraysAsListSerializer());
            kryo.set(localKryo);
        }
        return localKryo;
    }

    /**
     * Used to copy ArrayList in inner Collections class.
     */
    public static class ArraysAsListSerializer extends CollectionSerializer {
        @Override
        public Collection copy(Kryo kryo, Collection original) {
            return super.copy(kryo, new ArrayList(original));
        }
    }
}
