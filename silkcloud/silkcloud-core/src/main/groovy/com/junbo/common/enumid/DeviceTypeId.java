/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.enumid;

import com.junbo.common.id.IdResourcePath;

/**
 * Created by minhao on 5/8/14.
 */
@IdResourcePath("/device-types/{0}")
public class DeviceTypeId extends EnumId {
    public DeviceTypeId() {}
    public DeviceTypeId(String value) {
        super(value);
    }
}
