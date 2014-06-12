/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.id;

/**
 * Created by liangfu on 5/12/14.
 */
@IdResourcePath(value = "/master-key/{0}",
                resourceType = "master-key",
                regex = "/master-key/(?<id>[0-9A-Za-z]+)")
public class MasterKeyId extends Id {

    public MasterKeyId() {}
    public MasterKeyId(Long value) {
        super(value);
    }
}
