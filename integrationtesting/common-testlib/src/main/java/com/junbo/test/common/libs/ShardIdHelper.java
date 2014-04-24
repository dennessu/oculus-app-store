/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common.libs;

import com.junbo.common.id.UserId;
import com.junbo.sharding.*;
import com.junbo.test.common.exception.TestException;

/**
 * Created by Yunlong on 4/23/14.
 */
public class ShardIdHelper {

    public ShardIdHelper() {
    }

    public static int getShardIdByUid(String uid) {
        DefaultShardAlgorithm shardAlgorithm = new DefaultShardAlgorithm();
        //Get AvailableShards value from common\common-sharding\src\main\resources\spring\sharding-context.xml
        shardAlgorithm.setAvailableShards(2);
        if (uid != null && !uid.isEmpty()) {
            return shardAlgorithm.shardId(IdConverter.hexStringToId(UserId.class, uid));
        } else {
            throw new TestException("user Id can't be empty!");
        }
    }

}
