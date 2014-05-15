/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.id;

/**
 * Created by minhao on 4/26/14.
 */
@IdResourcePath(value = "/payment-instrument-types/{0}", regex = "/payment-instrument-types/(?<id>[0-9A-Z]+)")
public class PITypeId extends Id {
    public PITypeId() {}

    public PITypeId(long value) {
        super(value);
    }
}
