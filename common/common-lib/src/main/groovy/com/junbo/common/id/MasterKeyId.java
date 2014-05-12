/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.id;

/**
 * Created by liangfu on 5/12/14.
 */
@IdResourcePath("/master-key/{0}")
public class MasterKeyId extends Id {
    public MasterKeyId() {}
    public MasterKeyId(long value) {
        super(value);
    }
}
