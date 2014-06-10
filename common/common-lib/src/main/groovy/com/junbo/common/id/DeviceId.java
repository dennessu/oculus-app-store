/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.id;

/**
 * Created by liangfu on 4/3/14.
 */
@IdResourcePath(value = "/devices/{0}",
                resourceType = "devices",
                regex = "/devices/(?<id>[0-9A-Z]+)")
public class DeviceId extends Id {
    public DeviceId(){
    }

    public DeviceId(Long value) {
        super(value);
    }
}
