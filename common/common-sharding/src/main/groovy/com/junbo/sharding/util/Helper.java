/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.sharding.util;

/**
 * Created by haomin on 14-3-14.
 */
public class Helper {
    private static ThreadLocal<Integer> currentShardId = new ThreadLocal<Integer>();

    private Helper() {}

    /**
     * This shardId calculation is based on below Id format.
     *
     * Id generator proposed by oculus rift, it will use 48 bits
     * CCCC CCCC CCCC CCCC CCCC CCCC CCCC CCCC CCSS SSSS SSDD DDVV
     *    C is 34 bits of shuffled sequential ID
     *    S is 8 bits of shard ID
     *    D is 4 bits of data-center ID
     *    V is the ID version (hard coded 0)
     *    V is the least significant bits (hard coded 0 to indicate positive)
     *
     * order Id generator proposed by oculus rift, it will use 40 bits
     * 0CCC CCCC CCCC CCCC CCCC CCCC CCSS SSSS SSDD DDVV
     *    C is 25 bits of shuffled sequential ID
     *    S is 8 bits of shard ID
     *    D is 4 bits of data-center ID
     *    V is the ID version (hard coded 00)
     *    V is the least significant bits
     *
     * @param obj
     * @param cls
     * @return shardId
     */
    public static int calcShardId(Object obj, Class cls) {
        if(cls == Long.class) {
            Long id = (Long)obj;
            int shardId = (int)((id >> 6) & 0xff);
            if (shardId < 0 || shardId > 255) {
                throw new RuntimeException("invalid shardId value: " + shardId);
            }

            return shardId;
        }
        else if(cls == String.class) {
            String strKey = (String) obj;

            Long h = 0L;
            for (int i = 0; i < strKey.length(); i++) {
                h = 31 * h + ((int) strKey.charAt(i));
            }

            return calcShardId(h, Long.class);
        }
        else {
            throw new RuntimeException("current shardId only support Long and String");
        }
    }

    public static void setCurrentShardId(int shardId) {
        currentShardId.set(shardId);
    }

    public static int fetchCurrentThreadLocalShardId() {
        if (currentShardId.get() == null) {
            throw new RuntimeException("current shardId hasn't been set.");
        }

        return currentShardId.get().intValue();
    }
}
