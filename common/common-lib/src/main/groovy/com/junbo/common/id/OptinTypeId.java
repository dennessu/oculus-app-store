/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.id;

/**
 * Created by liangfu on 4/17/14.
 */
@IdResourcePath("/optin-type/{0}")
public class OptinTypeId extends Id {
    public OptinTypeId() {}

    public OptinTypeId(long value) {
        super(value);
    }
}
