/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.id;

/**
 * Created by liangfu on 3/13/14.
 */
@IdResourcePath(value = "/user-device-pairs/{0}", regex = "/user-device-pairs/(?<id>[0-9A-Z]+)")
public class UserDeviceId extends Id {
    public UserDeviceId() {}
    public UserDeviceId(long value) {
        super(value);
    }
}
