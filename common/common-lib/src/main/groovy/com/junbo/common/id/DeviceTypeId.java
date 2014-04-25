/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.id;

/**
 * Created by xiali_000 on 4/21/2014.
 */
@IdResourcePath("/device-types/{0}")
public class DeviceTypeId extends Id {
    public DeviceTypeId(){
    }

    public DeviceTypeId(Long value) {
        super(value);
    }
}
